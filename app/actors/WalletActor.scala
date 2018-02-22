package actors

import java.net.InetAddress
import java.nio.file.Paths
import java.util

import actors.models.InitiateBlockChain
import akka.actor.Actor
import org.bitcoinj.core._
import org.bitcoinj.crypto.KeyCrypterException
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.RegTestParams
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors
import com.google.inject.Inject
import com.google.inject.name.Named
import loggers._
import org.bitcoinj.core.listeners.PeerDataEventListener
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.store.{H2FullPrunedBlockStore, MemoryBlockStore}

import scala.concurrent.ExecutionContext

@Named("WalletActor")
class WalletActor @Inject()(
                           logger : WalletLogger
                           )(implicit ec: ExecutionContext) extends Actor {

  // Which network should we use
  val bitcoinNetwork = "regtest"
  // Figure out which network we should connect to. Each one gets its own set of files.
  val networkParams : NetworkParameters = bitcoinNetwork match {
    case "testnet" => TestNet3Params.get()
    case "regtest" => RegTestParams.get()
    case "mainnet" => logger.MainnetConnected
      MainNetParams.get()
  }
  val peerGroupContext = new Context(networkParams)
  val tablePath = Paths.get(".").resolve("BlockChainDB").normalize().toAbsolutePath.toString
  val blockStore = bitcoinNetwork match {
    case "regtest" => new MemoryBlockStore(networkParams)
    case "testnet" => new H2FullPrunedBlockStore(networkParams, tablePath, 1000)
  }
  val blockChain = new BlockChain(peerGroupContext, blockStore )
  var peerGroup : PeerGroup = new PeerGroup(peerGroupContext, blockChain)

  val walletListener = new WalletCoinsReceivedEventListener {
    override def onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin): Unit = ???
  }

  val blockChainListener = new PeerDataEventListener {
    override def onChainDownloadStarted(peer: Peer, blocksLeft: Int): Unit = logger.BlockChainDownLoadStart(blocksLeft)

    override def onPreMessageReceived(peer: Peer, m: Message): Message = ???

    override def getData(peer: Peer, m: GetDataMessage): util.List[Message] = ???

    override def onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock, blocksLeft: Int): Unit = {
      if (blocksLeft % 10 == 0) logger.BlockChainCount(blocksLeft)
      if (blocksLeft == 0) logger.BlockChainDownLoadFinished
    }
  }

  override def receive: Receive = {
    case init : InitiateBlockChain =>
      Context.propagate(peerGroupContext)
      peerGroup.setUserAgent("Btc Wallet", "0.0")
      peerGroup.setStallThreshold(10000, 1)
      peerGroup.setMaxConnections(30)
      bitcoinNetwork match {
        case "regtest" =>
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 8333))
          logger.PeerConnected("8333")
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 8444))
          logger.PeerConnected("8444")
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 8555))
          logger.PeerConnected("8555")
          peerGroup.start()
          logger.RegtestConnected
        case "testnet" =>
          peerGroup.addPeerDiscovery(new DnsDiscovery(networkParams) )
          peerGroup.start()
          logger.TestnetConnected
        case "mainnet" => ???
      }
  }
}

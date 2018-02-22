package actors
import java.net.InetAddress
import java.nio.file.Paths

import loggers._
import org.bitcoinj.core._
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.{MainNetParams, RegTestParams, TestNet3Params}
import org.bitcoinj.store.{H2FullPrunedBlockStore, MemoryBlockStore}

trait BlockchainStartup {
  val network = "regtest"
  // Figure out which network we should connect to. Each one gets its own set of files.
  val networkParams : NetworkParameters = network match {
    case "testnet" => TestNet3Params.get()
    case "regtest" => RegTestParams.get()
    case "mainnet" => MainNetParams.get()
  }
  val peerGroupContext = new Context(networkParams)
  val tablePath = Paths.get(".").resolve("BlockChainDB").normalize().toAbsolutePath.toString
  val blockStore = network match {
    case "regtest" => new MemoryBlockStore(networkParams)
    case "testnet" => new H2FullPrunedBlockStore(networkParams, tablePath, 1000)
  }
  val blockChain = new BlockChain(peerGroupContext, blockStore )
  val peerGroup : PeerGroup = new PeerGroup(peerGroupContext, blockChain)

  def startBlockchain(logger: WalletLogger): Unit = {
    Context.propagate(peerGroupContext)
    peerGroup.setUserAgent("Btc Wallet", "0.0")
    peerGroup.setStallThreshold(10000, 1)
    peerGroup.setMaxConnections(30)
    network match {
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

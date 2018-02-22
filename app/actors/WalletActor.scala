package actors

import java.util

import actors.models.{AddWallet, InitiateBlockChain, WalletInfo}
import akka.actor.Actor
import org.bitcoinj.core._
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import com.google.inject.Inject
import com.google.inject.name.Named
import loggers._
import org.bitcoinj.core.listeners.PeerDataEventListener
import org.spongycastle.util.encoders.Hex
import scala.collection.JavaConverters._

import scala.concurrent.ExecutionContext

@Named("WalletActor")
class WalletActor @Inject()(
                           logger : WalletLogger
                           )(implicit ec: ExecutionContext) extends Actor with BlockchainStartup {

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

  /*
  * addWallet / all associated blocks will be added to wallet.
  * */
  def addWallet(walletInfo : WalletInfo): Unit = {
    val seqECKEY = seqAsJavaList(Seq(ECKey.fromPublicOnly(Hex.decode(walletInfo.publicKey))))
    val jWallet = Wallet.fromKeys(networkParams, seqECKEY)
    jWallet.addCoinsReceivedEventListener(walletListener)
    jWallet.setAcceptRiskyTransactions(true)
    peerGroup.addWallet(jWallet)
    logger.StartedWatchingWallet(walletInfo)
  }

  override def receive: Receive = {
    case InitiateBlockChain => startBlockchain(logger)
    case AddWallet(walletInfo) => addWallet(walletInfo)
  }
}

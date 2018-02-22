package loggers

import actors.models.WalletInfo
import com.google.inject.Singleton
import play.api.Logger

@Singleton
class WalletLogger {
  def BlockChainDownLoadFinished = logger.info("[Blockchain] Blockchain download complete.")

  def BlockChainCount(blocksLeft: Int) = logger.info(s"[Blockchain] Blockchain download in progress. blocksLeft=$blocksLeft")

  def BlockChainDownLoadStart(blocksLeft: Int): Unit = logger.info(s"[Blockchain] Blockchain download starts. blocksLeft=$blocksLeft")

  def NetworkConnectionFailed: Unit = logger.error("[Network] Failed to connect to a network.")

  def MainnetConnected: Unit = logger.info("[Network] Mainnet connected.")

  def RegtestConnected: Unit = logger.info("[Network] Regtest connected.")

  def TestnetConnected: Unit = logger.info("[Network] Testnet connected.")

  def PeerConnected(port: String): Unit = logger.info(s"[Network] Peer connected. $port")

  def StartedWatchingWallet(walletInfo: WalletInfo): Unit = logger.info(s"[Wallet] Started watching wallet. pubKey=${walletInfo.publicKey}")

  private val logger: Logger = Logger("[BTC_WALLET]")
}

import actors.models.InitiateBlockChain
import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named

import scala.concurrent.ExecutionContext

class StartupWalletLoader @Inject()(
                                     @Named("WalletActor") walletClient : ActorRef
                                   )(implicit ec: ExecutionContext) {
  def initiateBlockChain = walletClient ! InitiateBlockChain
  initiateBlockChain
}


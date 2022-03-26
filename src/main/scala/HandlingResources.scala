import zio._

object HandlingResources {

  val finalizer =
    UIO.effectTotal(println("Finalizing"))

  val finalized = IO.fail("failed").ensuring(finalizer)
}

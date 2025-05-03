package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterRoutes
import dev.zio.quickstart.download.DownloadRoutes
import dev.zio.quickstart.greet.GreetingRoutes
import dev.zio.quickstart.users.{
  InmemoryUserRepo,
  PersistentUserRepo,
  UserRoutes
}
import zio._
import zio.http._

object MainApp extends ZIOAppDefault:

  def run =
    for {
      port <- System.env("PORT").map(_.map(_.toInt).getOrElse(8080))
      _ <- Server
        .serve(
          GreetingRoutes() ++ DownloadRoutes() ++ CounterRoutes() ++ UserRoutes()
        )
        .provide(
          Server.defaultWithPort(port),

          // An layer responsible for storing the state of the `counterApp`
          ZLayer.fromZIO(Ref.make(0)),

          // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
          InmemoryUserRepo.layer
        )
    } yield ()

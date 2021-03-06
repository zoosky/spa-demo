package app.root

import app.root.components.Root
import app.root.pages.spatutorial.client.components.GlobalStyles
import app.root.pages.spatutorial.client.logger._
import app.root.pages.spatutorial.client.modules._
import app.root.pages.spatutorial.client.services.{SPACircuit, Todos}
import diode.data.Pot
import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{ReactComponentU, ReactDOM, ReactElement, TopNode}
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object TodoLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    val todoWrapper: ReactConnectProxy[Pot[Todos]] = SPACircuit.connect(_.todos)
    val todoRender: (Any) => ReactComponentU[(ModelProxy[Pot[Todos]]) => ReactElement, Pot[Todos], Any, TopNode] = _ => todoWrapper(Todo(_))
    // wrap/connect components to the circuit
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap(_.motd)(proxy => Dashboard(ctl, proxy)))
      | staticRoute("#todo", TodoLoc) ~> renderR(todoRender )
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  val todoCountWrapper: ReactConnectProxy[Option[Int]] = SPACircuit.connect(_.todos.map(_.items.count(!_.completed)).toOption)
  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial")),
          <.div(^.className := "collapse navbar-collapse",
            // connect menu to model, because it needs to update when the number of open todos changes
            todoCountWrapper(proxy => MainMenu(c, r.page, proxy))
          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // tell React to render the router in the document body
    //ReactDOM.render(router(), dom.document.getElementById("root"))
    ReactDOM.render(Root.themedView(), dom.document.getElementById("joco"))
  }
}

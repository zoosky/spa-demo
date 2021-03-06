package app.root.components

import app.root.AppRouter
import chandu0101.scalajs.react.components.materialui.{Mui, MuiMuiThemeProvider}
import japgolly.scalajs.react.ReactComponentB

object Root {



    // Set up a theme based on default light theme, but replacing cyan with
    // blueGrey
    val baseTheme = Mui.Styles.LightRawTheme
    val theme = Mui.Styles.getMuiTheme(
      baseTheme.copy(palette =
        baseTheme.palette
          .copy(primary1Color = Mui.Styles.colors.blueGrey500)
          .copy(primary2Color = Mui.Styles.colors.blueGrey700)
          .copy(accent1Color = Mui.Styles.colors.deepOrangeA200)
      )
    )

    // Our top-level component, display pages based on URL, with app bar and navigation
    //val router = DemoRoutes.router
    val router = AppRouter.router

    // Need to wrap our top-level router component in a theme for Material-UI to work
    val themedView = ReactComponentB[Unit]("themedView").render(p =>
      MuiMuiThemeProvider(muiTheme = theme)(
        router()
      )
    ).build

    // Finally, render the themed top-level view to the predefined HTML div with id "container"
  //  themedView() render dom.document.getElementById("container")

}

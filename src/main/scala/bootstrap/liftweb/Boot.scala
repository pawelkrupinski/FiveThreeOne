package bootstrap.liftweb

import net.liftweb._
import common._
import http._
import sitemap._
import mapper._

class Boot extends Logger {

  def boot {

    // where to search snippet
    LiftRules.addToPackages("net.pawel")

    // Build SiteMap
    val menuBuilder = List.newBuilder[Menu]

    menuBuilder ++= List[Menu](
      Menu.i("Home") / "index"
    )
//
//    LiftRules.liftRequest.append {
//      case Req("classpath" :: _, _, _) => true
//      case Req("ajax_request" :: _, _, _) => true
//      case Req("favicon" :: Nil, "ico", GetRequest) => false
//      case Req(_, "css", GetRequest) => false
//      case Req(_, "js", GetRequest) => false
//    }

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(menuBuilder.result: _*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
  }
}

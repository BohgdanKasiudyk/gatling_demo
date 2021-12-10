package computerdatabase
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class TestSimulation extends Simulation{
  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scenario1 = scenario("get roots").exec(
    http("main root").get("/")
      .check(
        status is 200,
        regex("""\d+ computers found""")
      )
  ).exec(
    http("new root")
      .get("/computers/new")
      .check(
        status is 200,
        regex("""Add a computer""")

      )
  ).pause(5)

  val scenario2 = scenario("amount of elements on page").exec(
    http("page 2, 30 elems ")
      .get("/computers?p=2&n=30")
      .check( status is 200)

  ).pause(3)

  val scenario3 = scenario("order by").exec(
    http("order name by acs")
      .get("/computers?p=0&n=10&s=name")
      .check(status is 200)
  ).pause(3)
    .exec(
      http("order by company desc")
        .get("/computers?p=2&n=20&s=companyName&d=desc")
        .check(status is 200)
    ).pause(3)

  setUp(
    scenario1.inject(constantUsersPerSec(1) during(1 minute))
      .protocols(httpProtocol),
    scenario2.inject(rampUsersPerSec(1).to(2).during(15.seconds).randomized)
      .protocols(httpProtocol)
    ,
    scenario3.inject(atOnceUsers(1)).protocols(httpProtocol)
  )

}

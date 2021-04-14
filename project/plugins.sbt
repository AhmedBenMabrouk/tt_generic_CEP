// This plugin represents functionality that is to be added to sbt in the future
dependencyOverrides += "org.scala-sbt" % "sbt" % "1.1.2"
logLevel := Level.Warn
resolvers += Resolver.typesafeRepo("releases")

//addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.4.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
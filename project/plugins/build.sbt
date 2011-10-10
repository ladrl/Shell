libraryDependencies += "org.scala-tools.sbt" %% "sbt-android-plugin" % "0.6.0-SNAPSHOT"

resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/releases")
  val pattern = Patterns(false, "[organisation]/[module]/[sbtversion]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]")
  Resolver.url("Typesafe Repository", typesafeRepoUrl)(pattern)
}

libraryDependencies <<= (libraryDependencies, sbtVersion) { (deps, version) => 
  deps :+ ("com.typesafe.sbteclipse" %% "sbteclipse" % "1.3-RC3" extra("sbtversion" -> version))
}

resolvers += "gseitz@github" at "http://gseitz.github.com/maven/"

libraryDependencies += "com.github.gseitz" %% "sbt-protobuf" % "0.2" // for sbt-0.10.1
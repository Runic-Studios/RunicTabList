dependencies {
    compileOnly(commonLibs.spigot)
    compileOnly(commonLibs.paper)
    compileOnly(commonLibs.protocollib)
    compileOnly(commonLibs.luckperms)
    compileOnly(project(":Projects:Common"))
    compileOnly(project(":Projects:Core"))
    compileOnly(project(":Projects:Database"))
    compileOnly(project(":Projects:Guilds"))
}
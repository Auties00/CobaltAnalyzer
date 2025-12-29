package it.auties.analyzer

import org.openqa.selenium.devtools.v143.debugger.Debugger
import org.openqa.selenium.devtools.v143.network.Network
import java.util.Optional.empty

val whatsappKeys: Keys = Keys()

fun main() {
    val driver = initialize()
    val tools = driver.devTools
    tools.createSession()
    tools.send(Debugger.enable(empty()))
    tools.send(Debugger.setBreakpointsActive(true))

    tools.addListener(Debugger.scriptParsed(), { scriptParsedEvent ->
        onWhatsappScriptLoaded(tools, scriptParsedEvent, "\"AES-GCM\",!1")
    })

    tools.addListener(Debugger.paused(), { pausedEvent ->
        onBreakpointTriggered(tools, pausedEvent)
    })

    tools.send(Network.enable(empty(), empty(), empty(), empty(), empty()))

    tools.addListener(Network.webSocketFrameSent(), { webSocketFrameSentEvent ->
        handleSentMessage(webSocketFrameSentEvent)
    })

    tools.addListener(Network.webSocketFrameReceived(), { webSocketFrameReceivedEvent ->
        handleReceivedMessage(webSocketFrameReceivedEvent)
    })

    driver["https://web.whatsapp.com/"]

    println("Press Enter to exit...")
    readlnOrNull()

    driver.quit()
}
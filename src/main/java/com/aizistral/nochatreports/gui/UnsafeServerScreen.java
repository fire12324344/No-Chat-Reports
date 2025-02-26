package com.aizistral.nochatreports.gui;

import com.aizistral.nochatreports.NoChatReportsClient;
import com.aizistral.nochatreports.config.NCRConfig;
import com.aizistral.nochatreports.core.ServerSafetyState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * This is the screen user gets thrown into when they try to enter server that refuses to let them in
 * without exposing account's public key. From here the user can choose not to play on the server, or
 * agree to expose the key and send only signed messages. In the latter case they get reconnected to
 * the server and mod will not prevent the key from being sent. Optional checkbox is provided to add
 * the server to persistent whitelist in the config, to remove the need of going through this screen
 * every time when entering that specific server.
 *
 * @author Aizistral
 */

@Environment(EnvType.CLIENT)
public final class UnsafeServerScreen extends WarningScreen {
	private static final Component TITLE = Component.translatable("gui.nochatreports.unsafe_server.header").withStyle(ChatFormatting.BOLD);
	private static final Component CONTENT = Component.translatable("gui.nochatreports.unsafe_server.contents");
	private static final Component CHECK = Component.translatable("gui.nochatreports.unsafe_server.check");
	private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);

	private final Screen previous = new TitleScreen();
	private final Screen joinMultiplayer = new JoinMultiplayerScreen(new TitleScreen());

	public UnsafeServerScreen() {
		super(TITLE, CONTENT, CHECK, NARRATION);
	}

	@Override
	protected void initButtons(int i) {
		this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + i, 150, 20, CommonComponents.GUI_PROCEED, button -> {
			ServerAddress address = ServerSafetyState.getLastServerAddress();

			if (address != null) {
				if (this.stopShowing.selected()) {
					NCRConfig.getServerWhitelist().getList().add(address.getHost() + ":" + address.getPort());
					NCRConfig.getServerWhitelist().saveFile();
				}

				ServerSafetyState.setAllowsUnsafeServer(true);
				NoChatReportsClient.reconnectLastServer();
			}
		}));
		this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, CommonComponents.GUI_BACK, button -> {
			this.minecraft.setScreen(this.joinMultiplayer);
		}));
	}

}

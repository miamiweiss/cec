package com.manuelweiss.cec.model.parameter;

import java.util.EnumSet;

import static java.util.EnumSet.allOf;

public enum ParameterType {

	POWER_STATUS(allOf(PowerStatus.class)),
	SYSTEM_AUDIO_STATUS(allOf(SystemAudioStatus.class)),
	AUDIO_STATUS(allOf(AudioStatus.class)),
	UI_COMMAND(allOf(UICommand.class)),
	MENU_STATE(allOf(MenuState.class)),
	RAW(allOf(RawParameter.class));

	private final EnumSet<? extends Parameter> enumSet;

	ParameterType(EnumSet<? extends Parameter> enumSet) {
		this.enumSet = enumSet;
	}

	public EnumSet<? extends Parameter> getEnumSet() {
		return enumSet;
	}

}

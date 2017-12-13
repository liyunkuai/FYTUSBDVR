/**
 * ��Ȩ�������������Ƽ����޹�˾
 * ���:	 �»���
 * ���룺�������з���/Android��
 * ���ڣ�2015��1��1��
 */

package com.syu.dvr.utils;

public class FinalChip {
	public static final int CHIP_Null					= 0;
	public static final int CHIP_MST786					= 1;	// MStar 786
	public static final int CHIP_SPHE8700				= 2;	// Sunplus 8700
	public static final int CHIP_RKPX3					= 3;	// RockChip Px3
	public static final int CHIP_SOFIA					= 4;	// RockChip&Intel Sofia216
	public static final int CHIP_SG9832					= 5;	// SG9832-4G
	public static final int CHIP_RKPX5					= 6;	// RockChip Px5
	
	public static final int PLATFORM_Null				= 0;
	public static final int PLATFORM_5002				= 1;	// MStar 786 + 8288
	public static final int PLATFORM_5003				= 2;	// Sunplus 8700
	public static final int PLATFORM_5005				= 3;	// MStar 786 + 3702
	public static final int PLATFORM_5006				= 4;	// CHIP_RKPX3+3702
	public static final int PLATFORM_5007				= 5;	// Sunplus 8702
	public static final int PLATFORM_5008				= 6;	// CHIP_RKPX3+8288
	public static final int PLATFORM_6006				= 7;	// CHIP_RKPX3+STM32ǰװ
	public static final int PLATFORM_5009				= 8;	// CHIP_SOFIA˫����
	public static final int PLATFORM_2009				= 9;	// CHIP_SOFIA+STM32���Ӿ�
	public static final int PLATFORM_5011				= 10;	// CHIP_SOFIA+8288A
	public static final int PLATFORM_6021				= 11;	// CHIP_SOFIA����
	public static final int PLATFORM_6023				= 12;	// CHIP_SOFIA+8288A����
	public static final int PLATFORM_6013				= 13;	// CHIP_SOFIA+8288A
	public static final int PLATFORM_6011				= 14;	// CHIP_SOFIA_WF+BD8811+T132
	public static final int PLATFORM_6012				= 15;	// CHIP_SOFIA_3G+BD8811+T132
	public static final int PLATFORM_6022				= 16;	// CHIP_SOFIA+3702 ����
	public static final int PLATFORM_6025				= 17;	// CHIP_SG9832
	public static final int PLATFORM_6121				= 18;	// CHIP_SOFIA_WIFI+3702+2825(����)
	public static final int PLATFORM_6122				= 19;	// CHIP_SOFIA_3G+3702+2825(����)
	public static final int PLATFORM_6111				= 20;	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final int PLATFORM_6112				= 21;	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final int PLATFORM_800				= 22;	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final int PLATFORM_801				= 23;	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final int PLATFORM_6321				= 24;	// CHIP_SOFIA+System
	public static final int PLATFORM_6015				= 25;	// CHIP_SG9832
	public static final int PLATFORM_802				= 26;	// CHIP_SG9832����
	public static final int PLATFORM_803				= 27;	// CHIP_SG9832����_�޻���һ��
	public static final int PLATFORM_804				= 28;	// CHIP_SOFIA_3G+3702+2825(����)
	public static final int PLATFORM_805				= 29;	// CHIP_SOFIA_3G+3702+2825(����)
	public static final int PLATFORM_6112S				= 30;	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final int PLATFORM_6121S				= 31;	// CHIP_SOFIA_WIFI+3702+2825(����)
	public static final int PLATFORM_6111S				= 32;	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final int PLATFORM_6122S				= 33;	// CHIP_SOFIA_3G+3702+2825(����)
	public static final int PLATFORM_800S				= 34;	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final int PLATFORM_801S				= 35;	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final int PLATFORM_804S				= 36;	// CHIP_SOFIA_3G+3702+2825(����)
	public static final int PLATFORM_805S				= 37;	// CHIP_SOFIA_3G+3702+2825(����)
	public static final int PLATFORM_6026				= 38;	// CHIP_RKPX5+8288
	
	public static final String BSP_PLATFORM_Null		= "";
	public static final String BSP_PLATFORM_5002		= "5002";	// MStar 786 + 8288
	public static final String BSP_PLATFORM_5003		= "5003";	// Sunplus 8700
	public static final String BSP_PLATFORM_5005		= "5005";	// MStar 786 + 3702
	public static final String BSP_PLATFORM_5006		= "5006";	// CHIP_RKPX3+3702
	public static final String BSP_PLATFORM_5007		= "5007";	// Sunplus 8702
	public static final String BSP_PLATFORM_5008		= "5008";	// CHIP_RKPX3+8288
	public static final String BSP_PLATFORM_6006		= "6006";	// CHIP_RKPX3+STM32ǰװ
	public static final String BSP_PLATFORM_5009		= "5009";	// CHIP_SOFIA˫����
	public static final String BSP_PLATFORM_2009		= "2009";	// CHIP_SOFIA+STM32���Ӿ�
	public static final String BSP_PLATFORM_5011		= "5011";	// CHIP_SOFIA+8288A
	public static final String BSP_PLATFORM_6021		= "6021";	// CHIP_SOFIA+3702 ���� (common board)
	public static final String BSP_PLATFORM_6023		= "6023";	// CHIP_SOFIA+8288A����
	public static final String BSP_PLATFORM_6013		= "6013";	// CHIP_SOFIA+8288A
	public static final String BSP_PLATFORM_6011		= "6011";	// CHIP_SOFIA_WF+BD8811+T132
	public static final String BSP_PLATFORM_6012		= "6012";	// CHIP_SOFIA_3G+BD8811+T132
	public static final String BSP_PLATFORM_6022		= "6022";	// CHIP_SOFIA+3702 ����
	public static final String BSP_PLATFORM_6025		= "6025";	// CHIP_SG9832
	public static final String BSP_PLATFORM_6121		= "6121";	// CHIP_SOFIA_WIFI+3702+2825(����)
	public static final String BSP_PLATFORM_6122		= "6122";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String BSP_PLATFORM_6111		= "6111";	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_6112		= "6112";	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_800			= "800";	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_801			= "801";	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_6321		= "6321";	// CHIP_SOFIA+System
	public static final String BSP_PLATFORM_6015		= "6015";	// CHIP_9832
	public static final String BSP_PLATFORM_802			= "802";	// CHIP_SG9832����
	public static final String BSP_PLATFORM_803			= "803";	// CHIP_SG9832����_�޻���һ��
	public static final String BSP_PLATFORM_804			= "804";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String BSP_PLATFORM_805			= "805";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String BSP_PLATFORM_6112S		= "6112S";	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_6121S		= "6121S";	// CHIP_SOFIA_WIFI+3702+2825(����)
	public static final String BSP_PLATFORM_6111S		= "6111S";	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_6122S		= "6122S";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String BSP_PLATFORM_800S		= "800S";	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_801S		= "801S";	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final String BSP_PLATFORM_804S		= "804S";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String BSP_PLATFORM_805S		= "805S";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String BSP_PLATFORM_6026		= "6026";	// CHIP_RKPX5+8288
	
	public static final String MCU_PLATFORM_null		= "_00_";	// null
	public static final String MCU_PLATFORM_5002		= "_32_";	// 5002
	public static final String MCU_PLATFORM_5003		= "_70_";	// 5003
	public static final String MCU_PLATFORM_5005		= "_50_";	// 5005
	public static final String MCU_PLATFORM_5006		= "_60_";	// 5006
	public static final String MCU_PLATFORM_5007		= "_80_";	// 5007
	public static final String MCU_PLATFORM_5008		= "_61_";	// 5008
	public static final String MCU_PLATFORM_5009		= "_90_";	// 5009
	public static final String MCU_PLATFORM_5011		= "_90_";	// 5011
	public static final String MCU_PLATFORM_6021		= "_90_";	// 6021
	public static final String MCU_PLATFORM_6023		= "_90_";	// 6023
	public static final String MCU_PLATFORM_6013		= "_90_";	// 6013
	public static final String MCU_PLATFORM_6011		= "_90_";	// 6011
	public static final String MCU_PLATFORM_6012		= "_90_";	// 6012
	public static final String MCU_PLATFORM_6022		= "_90_";	// 6022
	public static final String MCU_PLATFORM_6121		= "_90_";	// 6121
	public static final String MCU_PLATFORM_6122		= "_90_";	// 6122
	public static final String MCU_PLATFORM_6111		= "_90_";	// 6111
	public static final String MCU_PLATFORM_6112		= "_90_";	// 6112
	public static final String MCU_PLATFORM_800			= "_90_";	// 800
	public static final String MCU_PLATFORM_801			= "_90_";	// 801
	public static final String MCU_PLATFORM_6321		= "_93_";	// 6321
	public static final String MCU_PLATFORM_6025		= "_25_";	// 4Gƽ̨
	public static final String MCU_PLATFORM_6015		= "_25_";	// 4Gƽ̨
	public static final String MCU_PLATFORM_802			= "_25_";	// 4Gƽ̨
	public static final String MCU_PLATFORM_803			= "_25_";	// CHIP_SG9832����_�޻���һ��
	public static final String MCU_PLATFORM_804			= "_90_";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String MCU_PLATFORM_805			= "_90_";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String MCU_PLATFORM_6112S		= "_90_";	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final String MCU_PLATFORM_6121S		= "_90_";	// CHIP_SOFIA_WIFI+3702+2825(����)
	public static final String MCU_PLATFORM_6111S		= "_90_";	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final String MCU_PLATFORM_6122S		= "_90_";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String MCU_PLATFORM_800S		= "_90_";	// CHIP_SOFIA_WIFI+3702+2825(��Ƭ)
	public static final String MCU_PLATFORM_801S		= "_90_";	// CHIP_SOFIA_3G+3702+2825(��Ƭ)
	public static final String MCU_PLATFORM_804S		= "_90_";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String MCU_PLATFORM_805S		= "_90_";	// CHIP_SOFIA_3G+3702+2825(����)
	public static final String MCU_PLATFORM_6026		= "_00_";	// CHIP_RKPX5+8288
	
	public final static int PLATFORM_TYPE_NULL	= 0;
	public final static int PLATFORM_TYPE_3702	= 1;
	public final static int PLATFORM_TYPE_8288	= 2;
	public final static int PLATFORM_TYPE_SYSTEM= 3;
}

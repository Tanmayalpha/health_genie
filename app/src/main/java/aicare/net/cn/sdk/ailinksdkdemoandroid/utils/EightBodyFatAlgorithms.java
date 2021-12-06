package aicare.net.cn.sdk.ailinksdkdemoandroid.utils;

import com.holtek.libHTBodyfat.HTBodyBasicInfo;
import com.holtek.libHTBodyfat.HTBodyResultAllBody;
import aicare.net.cn.sdk.ailinksdkdemoandroid.EightBodyfatAdc;

import java.util.Locale;


public class EightBodyFatAlgorithms {


    public EightBodyFatAlgorithms() {
    }

    private static class AlgorithmsHolder {
        private static EightBodyFatAlgorithms algorithmsUnit = new EightBodyFatAlgorithms();
    }


    public static EightBodyFatAlgorithms getInstance() {
        return AlgorithmsHolder.algorithmsUnit;

    }


    public EightBodyFatBean getAlgorithmsData(int algorithms, int sex, int height, float weight_kg, int age, EightBodyfatAdc eightBodyfatAdc) {
        EightBodyFatBean eightBodyFatBean = new EightBodyFatBean();
        switch (algorithms) {
            //和泰算法
            case 1:
            default:

                HTBodyBasicInfo basicInfo = new HTBodyBasicInfo(sex, height, weight_kg, age);
                basicInfo.htZAllBodyImpedance = eightBodyfatAdc.getAdcRightBody();
                basicInfo.htZLeftLegImpedance = eightBodyfatAdc.getAdcLeftFoot();
                basicInfo.htZRightLegImpedance = eightBodyfatAdc.getAdcRightFoot();
                basicInfo.htZLeftArmImpedance = eightBodyfatAdc.getAdcLeftHand();
                basicInfo.htZRightArmImpedance = eightBodyfatAdc.getAdcRightHand();
                basicInfo.htTwoLegsImpedance = eightBodyfatAdc.getAdcFoot();
                basicInfo.htTwoArmsImpedance = eightBodyfatAdc.getAdcHand();
                HTBodyResultAllBody resultTwoLegs = new HTBodyResultAllBody();
                int errorType = resultTwoLegs.getBodyfatWithBasicInfo(basicInfo);
                if (errorType == HTBodyBasicInfo.ErrorNone) {
                    eightBodyFatBean.setBmi(Adecimal(resultTwoLegs.htBMI));
                    eightBodyFatBean.setBmr((float) resultTwoLegs.htBMR);
                    eightBodyFatBean.setUvi((float) resultTwoLegs.htVFAL);
                    eightBodyFatBean.setBm(String.valueOf((float) resultTwoLegs.htBoneKg));
                    eightBodyFatBean.setBfr(Adecimal(resultTwoLegs.htBodyfatPercentage));
                    eightBodyFatBean.setVwc(Adecimal(resultTwoLegs.htWaterPercentage));
                    eightBodyFatBean.setRom(Adecimal(resultTwoLegs.htMusclePercentage));
                    eightBodyFatBean.setBodyAge(resultTwoLegs.htBodyAge);
                    eightBodyFatBean.setPp(Adecimal(resultTwoLegs.htProteinPercentage));
                    eightBodyFatBean.setSfr(Adecimal(resultTwoLegs.htBodyfatSubcut));
                    eightBodyFatBean.setFatMassBody(String.valueOf(resultTwoLegs.htBodyfatKgTrunk));
                    eightBodyFatBean.setFatMassLeftTop(String.valueOf(resultTwoLegs.htBodyfatKgLeftArm));
                    eightBodyFatBean.setFatMassLeftBottom(String.valueOf(resultTwoLegs.htBodyfatKgLeftLeg));
                    eightBodyFatBean.setFatMassRightTop(String.valueOf(resultTwoLegs.htBodyfatKgRightArm));
                    eightBodyFatBean.setFatMassRightBottom(String.valueOf(resultTwoLegs.htBodyfatKgRightLeg));
//                    eightBodyFatBean.setFatMass(resultTwoLegs.htBodyfatKg);
                    eightBodyFatBean.setMuscleMassBody(String.valueOf(resultTwoLegs.htMuscleKgTrunk));
                    eightBodyFatBean.setMuscleMassLeftTop(String.valueOf(resultTwoLegs.htMuscleKgLeftArm));
                    eightBodyFatBean.setMuscleMassLeftBottom(String.valueOf(resultTwoLegs.htMuscleKgLeftLeg));
                    eightBodyFatBean.setMuscleMassRightTop(String.valueOf(resultTwoLegs.htMuscleKgRightArm));
                    eightBodyFatBean.setMuscleMassRightBottom(String.valueOf(resultTwoLegs.htMuscleKgRightLeg));
//                    eightBodyFatBean.setMusleMass(resultTwoLegs.htMuscleKg);
//                    eightBodyFatBean.setStandardWeight(resultTwoLegs.htIdealWeightKg);
//                    eightBodyFatBean.setWeightWithoutFat(resultTwoLegs.htBodyfatFreeMass);
//                    eightBodyFatBean.setWeightControl((weight_kg - resultTwoLegs.htIdealWeightKg));
//                    eightBodyFatBean.setFatLevel(HealthyStatusUtil.ObesitylevelsStatus(weight_kg, (float) resultTwoLegs.htIdealWeightKg));
//                    double muscle = (resultTwoLegs.htMuscleKgLeftArm + resultTwoLegs.htMuscleKgLeftLeg + resultTwoLegs.htMuscleKgRightArm + resultTwoLegs.htMuscleKgRightLeg);
//                    eightBodyFatBean.setMusleMassLimbs(Adecimal(muscle / (height * height) * 10000));

                    eightBodyFatBean.setAdcFoot((resultTwoLegs.htZLeftLeg + resultTwoLegs.htZRightLeg));
                    eightBodyFatBean.setAdcHand((resultTwoLegs.htZLeftArm + resultTwoLegs.htZRightArm));
                    eightBodyFatBean.setAdcLeftHand(resultTwoLegs.htZLeftArm);
                    eightBodyFatBean.setAdcRightHand(resultTwoLegs.htZRightArm);
                    eightBodyFatBean.setAdcLeftFoot(resultTwoLegs.htZLeftLeg);
                    eightBodyFatBean.setAdcRightFoot(resultTwoLegs.htZRightLeg);
                    eightBodyFatBean.setAdcLeftBody(resultTwoLegs.htZAllBody);
                    eightBodyFatBean.setAdcRightBody(resultTwoLegs.htZAllBody);
                    eightBodyFatBean.setAdcRightHandLeftFoot((resultTwoLegs.htZRightArm + resultTwoLegs.htZLeftLeg));
                    eightBodyFatBean.setAdcLeftHandRightFoot((resultTwoLegs.htZLeftArm + resultTwoLegs.htZRightLeg));
                    eightBodyFatBean.setAdcBody(resultTwoLegs.htZAllBody);

                }


        }
        return eightBodyFatBean;
    }


    private float Adecimal(double data) {


        return Float.parseFloat(String.format(Locale.US, "%.1f", data));


    }
}

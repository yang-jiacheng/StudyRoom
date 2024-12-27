package com.lxy.studyroom.constant

import com.lxy.studyroom.BuildConfig
import com.lxy.studyroom.logic.network.ServiceCreator

object CommonConstant {

    const val BASE_TOKEN_NAME = "studyRoomToken"

    const val AGREEMENT = "${BuildConfig.API_HOST}userAgreement/agreementPolicyInfoPage2"

    const val POLICY = "${BuildConfig.API_HOST}userAgreement/agreementPolicyInfoPage1"

    const val ABOUT_US = "${BuildConfig.API_HOST}userAgreement/aboutSoftware"

    const val def_icon = "${BuildConfig.OSS_PREFIX}/upload/defPath.jpg"

    const val def_cover = "${BuildConfig.OSS_PREFIX}/upload/defCover.jpg"
}
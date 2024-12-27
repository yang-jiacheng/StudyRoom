package com.lxy.studyroom.logic

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.liveData
import com.lxy.studyroom.StudyRoomApplication
import com.lxy.studyroom.constant.CommonConstant
import com.lxy.studyroom.constant.UserConstant
import com.lxy.studyroom.enums.ResponseEnum
import com.lxy.studyroom.extension.toast
import com.lxy.studyroom.logic.dto.StudyRecordDTO
import com.lxy.studyroom.logic.model.*
import com.lxy.studyroom.ui.login.LoginActivity
import com.lxy.studyroom.util.DataStoreUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MultipartBody
import java.lang.Exception

object Repository {

    /**
     * 高阶函数，把网络请求放在 IO 线程， 统一 try catch 处理，返回LiveData
     */
    private fun <T> handleResponse(block: suspend () -> ResponseResult<T>) = run {
        liveData(Dispatchers.IO){
            var result: ResponseResult<T>
            try {
                result = block()
                if (result.code == ResponseEnum.NEED_LOGIN.code){

                    saveUserLoginStatus(false)
                    val intent = Intent(StudyRoomApplication.context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    StudyRoomApplication.context.startActivity(intent)
                }
            }catch (e: Exception){
                e.printStackTrace()
                result = ResponseResult()
                result.code = ResponseEnum.DATA_ERROR.code
                result.msg = ResponseEnum.DATA_ERROR.msg
            }
            emit(result)
        }
    }

    fun checkVersion() = handleResponse {
        StudyRoomNetwork.checkVersion()
    }

    /**
     * 所有图书馆
     */
    fun getAllLibrary() = handleResponse {
        StudyRoomNetwork.getAllLibrary()
    }

    /**
     * 图书馆详情
     */
    fun getClassifyDetail(classifyId: Int) = handleResponse {
        StudyRoomNetwork.getClassifyDetail(classifyId)
    }

    /**
     * 开始自习
     */
    fun startStudy(studyRecordDTO: StudyRecordDTO) = handleResponse {
        StudyRoomNetwork.startStudy(studyRecordDTO)
    }

    /**
     * 结束自习
     */
    fun stopStudy(recordId: Int) = handleResponse {
        StudyRoomNetwork.stopStudy(recordId)
    }

    /**
     * 学习记录详情
     */
    fun getLearningRecordDetail(recordId: Int) = handleResponse {
        StudyRoomNetwork.getLearningRecordDetail(recordId)
    }

    fun updateRecordToFinish() = handleResponse {
        StudyRoomNetwork.updateRecordToFinish()
    }

    /**
     * 提交学习时长
     */
    fun submitStudyDuration(recordId: Int,duration: Int) = handleResponse {
        StudyRoomNetwork.submitStudyDuration(recordId, duration)
    }

    /**
     * 获取笔记详情
     */
    fun getStudyNoteDetail(recordId: Int) = handleResponse {
        StudyRoomNetwork.getStudyNoteDetail(recordId)
    }

    /**
     * 获取笔记列表
     */
    fun getStudyNotes(page: Int,limit: Int) = handleResponse {
        StudyRoomNetwork.getStudyNotes(page, limit)
    }

    /**
     * 编辑笔记
     */
    fun saveStudyNote(recordId: Int,content: String,pic: String) = handleResponse {
        StudyRoomNetwork.saveStudyNote(recordId, content, pic)
    }

    fun getSelfRankings() = handleResponse {
        StudyRoomNetwork.getSelfRankings()
    }

    fun removeStudyNote(recordId: Int) = handleResponse {
        StudyRoomNetwork.removeStudyNote(recordId)
    }

    fun getStudyRecord(page: Int,limit: Int) = handleResponse {
        StudyRoomNetwork.getStudyRecord(page, limit)
    }

    fun upload(file :List<MultipartBody.Part>) = handleResponse {
        StudyRoomNetwork.upload(file)
    }

    /**
     * 利用协程，并发执行俩请求提高效率
     */
    fun getRoomDetailAndRecords(roomId: Int) = handleResponse {
        coroutineScope {
            val result: ResponseResult<RoomRecord>

            val room = async {
                StudyRoomNetwork.getRoomDetail(roomId)
            }
            val record = async {
                StudyRoomNetwork.getLearningRecords(roomId)
            }

            val roomResponse = room.await()
            val recordResponse = record.await()

            if (roomResponse.isSuccess() && recordResponse.isSuccess()){
                result = ResponseResult()
                result.code = ResponseEnum.SUCCESS.code
                result.data = RoomRecord(roomResponse.data!!,recordResponse.data)
            }else{
                throw RuntimeException("数据不对头")
            }

            result
        }
    }

    fun getUserRecordsAndStatistics(page: Int,limit: Int)= handleResponse {
        coroutineScope{
            val result: ResponseResult<UserStatistics>

            val user = async {
                StudyRoomNetwork.getSelfRankings()
            }
            val records = async {
                StudyRoomNetwork.getStudyRecord(page, limit)
            }

            val userResp = user.await()
            val recordsResp = records.await()
            if (userResp.isSuccess() && recordsResp.isSuccess()){
                result = ResponseResult()
                result.code = ResponseEnum.SUCCESS.code
                result.data = UserStatistics(userResp.data!!,recordsResp.data)
            }else{
                throw RuntimeException("数据不对头")
            }

            result
        }
    }



    /**
     * 排行榜和规则
     */
    fun getRankAndRule() = handleResponse {
        coroutineScope {
            val result: ResponseResult<Ranking>

            val rank = async {
                StudyRoomNetwork.getRankings()
            }
            val rule = async {
                StudyRoomNetwork.getRankingsRules()
            }

            val rankResponse = rank.await()
            val ruleResponse = rule.await()

            if (rankResponse.isSuccess() && ruleResponse.isSuccess()){
                result = ResponseResult()
                val rankList = rankResponse.data ?: ArrayList<UserRank>()
                val ruleData = ruleResponse.data ?: ""
                result.code = ResponseEnum.SUCCESS.code
                result.data = Ranking(rankList,ruleData)
            }else{
                throw RuntimeException("数据不对头")
            }

            result
        }
    }

    /**
     * 反馈
     */
    fun submitFeedBack(content: String,pic: String) = handleResponse {
        StudyRoomNetwork.submitFeedBack(content, pic)
    }

    fun getFeedBackList(page: Int,limit: Int,mine: Int) = handleResponse {
        StudyRoomNetwork.getFeedBackList(page, limit, mine)
    }

    fun getFeedBackDetail(id: Int) = handleResponse {
        StudyRoomNetwork.getFeedBackDetail(id)
    }


    fun getUserInfo() = handleResponse {
        StudyRoomNetwork.getUserInfo()
    }

    fun getUserInfoById(userId: Int) = handleResponse {
        StudyRoomNetwork.getUserInfoById(userId)
    }

    fun updatePassword(phone: String,verificationCode: String,password: String) = handleResponse {
        StudyRoomNetwork.updatePassword(phone, verificationCode, password)
    }

    fun updateUserInfo(name: String,gender: String,address: String,profilePath: String,coverPath: String) = handleResponse {
        StudyRoomNetwork.updateUserInfo(name, gender, address, profilePath, coverPath)
    }

    fun getVerificationCode(phone: String) = handleResponse {
        StudyRoomNetwork.getVerificationCode(phone)
    }

    fun loginByVerificationCode(phone: String,verificationCode: String) = handleResponse {
        StudyRoomNetwork.loginByVerificationCode(phone, verificationCode)
    }

    fun login(phone: String,password: String) = handleResponse {
        StudyRoomNetwork.login(phone, password)
    }

    fun logout() = handleResponse {
        StudyRoomNetwork.logout()
    }

    /**
     * 登录状态
     */
    fun getUserLoginStatus(): Boolean = DataStoreUtil.getBoolean(UserConstant.LOGIN_STATUS, false)

    fun saveUserLoginStatus(flag: Boolean) = DataStoreUtil.putBoolean(UserConstant.LOGIN_STATUS,flag)

    fun saveToken(token: String) = DataStoreUtil.putString(CommonConstant.BASE_TOKEN_NAME,token)

    /**
     * 用户协议与隐私政策是否被选中
     */
    fun getAgreePolicy(): Boolean = DataStoreUtil.getBoolean(UserConstant.AGREE_POLICY, false)

    fun saveAgreePolicy(flag: Boolean) = DataStoreUtil.putBoolean(UserConstant.AGREE_POLICY,flag)

    /**
     * 用户手机号
     */
    fun getUserPhone(): String = DataStoreUtil.getString(UserConstant.PHONE,"")

    fun saveUserPhone(phone: String) = DataStoreUtil.putString(UserConstant.PHONE,phone)

    /**
     * 用户id
     */
    fun getUserId(): Int = DataStoreUtil.getInt(UserConstant.ID,-1)

    fun saveUserId(userId: Int) = DataStoreUtil.putInt(UserConstant.ID,userId)

    /**
     * 用户密码
     */
    fun getUserPassword(): String = DataStoreUtil.getString(UserConstant.PASSWORD,"")

    fun saveUserPassword(password: String) = DataStoreUtil.putString(UserConstant.PASSWORD,password)




}
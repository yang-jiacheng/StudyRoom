package com.lxy.studyroom.logic

import com.lxy.studyroom.logic.dto.StudyRecordDTO
import com.lxy.studyroom.logic.network.ServiceCreator
import com.lxy.studyroom.logic.network.service.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object StudyRoomNetwork {

    /**
     * 协程，声明为挂起函数，请求成功或失败都会恢复被挂起的协程
     */
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(RuntimeException("Are you ok ?"))
                }
            })
        }
    }

    /**
     * 版本控制
     */
    private val versionControlService  = ServiceCreator.create<VersionControlService>()

    suspend fun checkVersion() = versionControlService.checkVersion().await()

            /**
     * 上传文件
     */
    private val resourcesService = ServiceCreator.create<ResourcesService>()

    suspend fun upload(file :List<MultipartBody.Part>) = resourcesService.upload(file).await()

    /**
     * 图书馆
     */
    private val libraryService = ServiceCreator.create<LibraryService>()

    suspend fun getAllLibrary() = libraryService.getAllLibrary().await()

    suspend fun getClassifyDetail(classifyId: Int) =
        libraryService.getClassifyDetail(classifyId).await()

    /**
     * 自习室
     */
    private val catalogService  = ServiceCreator.create<CatalogService>()

    private val studyStatisticsService  = ServiceCreator.create<StudyStatisticsService>()

    private val studyRecordService  = ServiceCreator.create<StudyRecordService>()

    suspend fun getRoomDetail(roomId: Int) = catalogService.getRoomDetail(roomId).await()

    suspend fun getLearningRecords(catalogId: Int) =
        catalogService.getLearningRecords(catalogId).await()

    suspend fun startStudy(studyRecordDTO: StudyRecordDTO) =
        catalogService.startStudy(studyRecordDTO).await()

    suspend fun stopStudy(recordId: Int) = catalogService.stopStudy(recordId).await()

    suspend fun updateRecordToFinish() = catalogService.updateRecordToFinish().await()

    suspend fun getLearningRecordDetail(recordId: Int) =
        catalogService.getLearningRecordDetail(recordId).await()

    suspend fun submitStudyDuration(recordId: Int,duration: Int) =
        studyStatisticsService.submitStudyDuration(recordId, duration).await()

    suspend fun getRankings() = studyStatisticsService.getRankings().await()

    suspend fun getRankingsRules() = studyStatisticsService.getRankingsRules().await()

    suspend fun getSelfRankings() = studyStatisticsService.getSelfRankings().await()

    /**
     * 自习记录、笔记
     */
    suspend fun getStudyNoteDetail(recordId: Int) = studyRecordService.getStudyNoteDetail(recordId).await()

    suspend fun getStudyNotes(page: Int,limit: Int) = studyRecordService.getStudyNotes(page, limit).await()

    suspend fun saveStudyNote(recordId: Int,content: String,pic: String) =
        studyRecordService.saveStudyNote(recordId, content, pic).await()

    suspend fun removeStudyNote(recordId: Int) = studyRecordService.removeStudyNote(recordId).await()

    suspend fun getStudyRecord(page: Int,limit: Int) = studyRecordService.getStudyRecord(page, limit).await()

    /**
     * 反馈
     */

    private val feedbackService = ServiceCreator.create<FeedbackService>()

    suspend fun submitFeedBack(content: String,pic: String) = feedbackService.submitFeedBack(content, pic).await()

    suspend fun getFeedBackList(page: Int,limit: Int,mine: Int) = feedbackService.getFeedBackList(page, limit, mine).await()

    suspend fun getFeedBackDetail(id: Int) = feedbackService.getFeedBackDetail(id).await()

    /**
     * 认证相关
     */
    private val tokenService  = ServiceCreator.create<TokenService>()

    suspend fun getVerificationCode(phone: String) = tokenService.getVerificationCode(phone).await()

    suspend fun login(phone: String,password: String) = tokenService.login(phone, password).await()

    suspend fun loginByVerificationCode(phone: String,verificationCode: String) =
        tokenService.loginByVerificationCode(phone, verificationCode).await()

    suspend fun logout() = tokenService.logout().await()

    /**
     * 用户相关
     */
    private val personalCenterService  = ServiceCreator.create<PersonalCenterService>()

    suspend fun updatePassword(phone: String,verificationCode: String,password: String) =
            personalCenterService.updatePassword(phone, verificationCode, password).await()

    suspend fun updateUserInfo(name: String,gender: String,address: String,profilePath: String,coverPath: String) =
        personalCenterService.updateUserInfo(name, gender, address, profilePath, coverPath).await()

    suspend fun getUserInfo() = personalCenterService.getUserInfo().await()

    suspend fun getUserInfoById(userId: Int) = personalCenterService.getUserInfoById(userId).await()



}
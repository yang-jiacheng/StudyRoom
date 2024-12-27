package com.lxy.studyroom.logic.model

import java.io.Serializable

data class RoomRecord(val room: LibraryRoomDetail,val records: List<StudyRecord>?) : Serializable

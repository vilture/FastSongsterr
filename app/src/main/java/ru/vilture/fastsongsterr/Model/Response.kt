package ru.vilture.fastsongsterr.Model

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("chordsPresent")
	val chordsPresent: Boolean? = null,

	@field:SerializedName("artist")
	var artist: Artist? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("tabTypes")
	val tabTypes: List<String?>? = null
)

data class Artist(

	@field:SerializedName("nameWithoutThePrefix")
	val nameWithoutThePrefix: String? = null,

	@field:SerializedName("useThePrefix")
	val useThePrefix: Boolean? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)

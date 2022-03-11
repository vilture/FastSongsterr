package ru.vilture.fastsongsterr.Model

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("chordsPresent")
	val chordsPresent: Boolean? = null,

	@field:SerializedName("artist")
	val artist: Artist? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("tabTypes")
	val tabTypes: List<String?>? = null
)

data class Artist(

	@field:SerializedName("nameWithoutThePrefix")
	val nameWithoutThePrefix: String? = null,

	@field:SerializedName("useThePrefix")
	val useThePrefix: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)

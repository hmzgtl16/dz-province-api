package com.example.dzprovinceapi.shared.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import org.springframework.data.domain.Page

class PageSerializer<T : Any>(
    private val elementSerializer: KSerializer<T>,
) : KSerializer<Page<T>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Page") {
            element(
                "content",
                ListSerializer(elementSerializer).descriptor,
            )
            element<Boolean>("empty")
            element<Boolean>("first")
            element<Boolean>("last")
            element<Int>("number")
            element<Int>("number-of-elements")
            element(
                "pageable",
                PageableSerializer().descriptor,
            )
            element<Int>("size")
            element(
                "sort",
                SortSerializer().descriptor,
            )
            element<Long>("total-elements")
            element<Int>("total-pages")
        }

    override fun serialize(
        encoder: Encoder,
        value: Page<T>,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor,
                0,
                ListSerializer(elementSerializer),
                value.content,
            )
            encodeBooleanElement(descriptor, 1, value.isEmpty)
            encodeBooleanElement(descriptor, 2, value.isFirst)
            encodeBooleanElement(descriptor, 3, value.isLast)
            encodeIntElement(descriptor, 4, value.number)
            encodeIntElement(descriptor, 5, value.numberOfElements)
            encodeSerializableElement(
                descriptor,
                6,
                PageableSerializer(),
                value.pageable,
            )
            encodeIntElement(descriptor, 7, value.size)
            encodeSerializableElement(
                descriptor,
                8,
                SortSerializer(),
                value.sort,
            )
            encodeLongElement(descriptor, 9, value.totalElements)
            encodeIntElement(descriptor, 10, value.totalPages)
        }
    }

    override fun deserialize(decoder: Decoder): Page<T> =
        throw UnsupportedOperationException("Deserializing Page is not supported")
}

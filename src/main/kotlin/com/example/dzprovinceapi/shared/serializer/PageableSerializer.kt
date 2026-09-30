/*
 * Copyright 2026 Hamza Gattal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dzprovinceapi.shared.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import org.springframework.data.domain.Pageable

class PageableSerializer : KSerializer<Pageable> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Pageable") {
            element<Long>("offset")
            element<Int>("page-number")
            element<Int>("page-size")
            element<Boolean>("paged")
            element("sort", SortSerializer().descriptor)
            element<Boolean>("unpaged")
        }

    override fun serialize(
        encoder: Encoder,
        value: Pageable,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.offset)
            encodeIntElement(descriptor, 1, value.pageNumber)
            encodeIntElement(descriptor, 2, value.pageSize)
            encodeBooleanElement(descriptor, 3, value.isPaged)
            encodeSerializableElement(
                descriptor,
                4,
                SortSerializer(),
                value.sort,
            )
            encodeBooleanElement(descriptor, 5, value.isUnpaged)
        }
    }

    override fun deserialize(decoder: Decoder): Pageable =
        throw UnsupportedOperationException("Deserializing Pageable is not supported")
}

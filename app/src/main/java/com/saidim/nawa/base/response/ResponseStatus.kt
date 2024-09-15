/*
 * Copyright 2018-present KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saidim.nawa.base.response

import com.saidim.nawa.base.response.ResultSource

/**
 * TODO：本类仅用作示例参考，请根据 "实际项目需求" 配置自定义的 "响应状态元信息"
 *
 * Create by KunMinX at 19/10/11
 */
class ResponseStatus {
    var responseCode = ""
        private set
    var isSuccess = true
        private set
    var source: Enum<*> = ResultSource.NETWORK
        private set

    constructor()
    constructor(responseCode: String, success: Boolean) {
        this.responseCode = responseCode
        isSuccess = success
    }

    constructor(responseCode: String, success: Boolean, source: Enum<*>) : this(responseCode, success) {
        this.source = source
    }
}
/*
 * YukiReflection - An efficient Reflection API for the Android platform built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiReflection
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/9/5.
 * This file is Modified by fankes on 2023/1/25.
 */
@file:Suppress("PropertyName")

package com.highcapable.yukireflection.finder.classes.data

import com.highcapable.yukireflection.finder.base.data.BaseRulesData
import com.highcapable.yukireflection.finder.base.rules.ModifierRules
import com.highcapable.yukireflection.finder.members.data.ConstructorRulesData
import com.highcapable.yukireflection.finder.members.data.FieldRulesData
import com.highcapable.yukireflection.finder.members.data.MemberRulesData
import com.highcapable.yukireflection.finder.members.data.MethodRulesData
import com.highcapable.yukireflection.finder.type.factory.NameConditions
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * [Class] ?????????????????????
 * @param fromPackages ??????????????????????????????
 * @param fullName ????????????
 * @param simpleName ????????????
 * @param singleName ????????????
 * @param fullNameConditions ??????????????????
 * @param simpleNameConditions ??????????????????
 * @param singleNameConditions ??????????????????
 * @param isAnonymousClass ?????????
 * @param isNoExtendsClass ??????????????????
 * @param isNoImplementsClass ??????????????????????????????
 * @param extendsClass ???????????????????????????
 * @param implementsClass ??????????????????????????????
 * @param enclosingClass ?????????????????? (??????) ????????????
 * @param memberRules [Member] ????????????????????????
 * @param fieldRules [Field] ????????????????????????
 * @param methodRules [Method] ????????????????????????
 * @param constroctorRules [Constructor] ????????????????????????
 */
@PublishedApi
internal class ClassRulesData internal constructor(
    var fromPackages: ArrayList<PackageRulesData> = arrayListOf(),
    var fullName: NameRulesData? = null,
    var simpleName: NameRulesData? = null,
    var singleName: NameRulesData? = null,
    var fullNameConditions: NameConditions? = null,
    var simpleNameConditions: NameConditions? = null,
    var singleNameConditions: NameConditions? = null,
    var isAnonymousClass: Boolean? = null,
    var isNoExtendsClass: Boolean? = null,
    var isNoImplementsClass: Boolean? = null,
    var extendsClass: ArrayList<String> = arrayListOf(),
    var implementsClass: ArrayList<String> = arrayListOf(),
    var enclosingClass: ArrayList<String> = arrayListOf(),
    var memberRules: ArrayList<MemberRulesData> = arrayListOf(),
    var fieldRules: ArrayList<FieldRulesData> = arrayListOf(),
    var methodRules: ArrayList<MethodRulesData> = arrayListOf(),
    var constroctorRules: ArrayList<ConstructorRulesData> = arrayListOf()
) : BaseRulesData() {

    /**
     * ???????????????????????????????????????
     * @param name ??????
     * @return [NameRulesData]
     */
    internal fun createNameRulesData(name: String) = NameRulesData(name)

    /**
     * ?????????????????????????????????????????????????????????
     * @param name ??????
     * @return [PackageRulesData]
     */
    internal fun createPackageRulesData(name: String) = PackageRulesData(name)

    /**
     * ?????? [Class.getSimpleName] ??? [Class.getName] ???????????????
     * @param instance ?????? [Class] ??????
     * @return [String]
     */
    internal fun classSingleName(instance: Class<*>) = instance.simpleName.takeIf { it.isNotBlank() }
        ?: instance.enclosingClass?.let { it.simpleName + instance.name.replace(it.name, newValue = "") } ?: ""

    /**
     * ?????????????????????????????????
     * @param name ??????
     * @param isOptional ???????????? - ?????????
     */
    inner class NameRulesData internal constructor(var name: String, var isOptional: Boolean = false) {

        /** [Class.getName] */
        internal val TYPE_NAME = 0

        /** [Class.getSimpleName] */
        internal val TYPE_SIMPLE_NAME = 1

        /** [Class.getSimpleName] or [Class.getName] */
        internal val TYPE_SINGLE_NAME = 2

        /**
         * ???????????? [Class] ??????
         * @param instance ?????? [Class] ??????
         * @param type ????????????
         * @return [Boolean]
         */
        internal fun equals(instance: Class<*>, type: Int) = when (type) {
            TYPE_NAME -> instance.name == name
            TYPE_SIMPLE_NAME -> instance.simpleName == name
            TYPE_SINGLE_NAME -> classSingleName(instance) == name
            else -> false
        }

        override fun toString() = "$name optional($isOptional)"
    }

    /**
     * ???????????????????????????????????????????????????
     * @param name ??????
     * @param isAbsolute ?????????????????? - ?????????
     */
    inner class PackageRulesData internal constructor(var name: String, var isAbsolute: Boolean = false) {
        override fun toString() = "$name absolute($isAbsolute)"
    }

    override val templates
        get() = arrayOf(
            fromPackages.takeIf { it.isNotEmpty() }?.let { "from:$it" } ?: "",
            fullName?.let { "fullName:[$it]" } ?: "",
            simpleName?.let { "simpleName:[$it]" } ?: "",
            singleName?.let { "singleName:[$it]" } ?: "",
            fullNameConditions?.let { "fullNameConditions:[existed]" } ?: "",
            simpleNameConditions?.let { "simpleNameConditions:[existed]" } ?: "",
            singleNameConditions?.let { "singleNameConditions:[existed]" } ?: "",
            modifiers?.let { "modifiers:${ModifierRules.templates(uniqueValue)}" } ?: "",
            isAnonymousClass?.let { "isAnonymousClass:[$it]" } ?: "",
            isNoExtendsClass?.let { "isNoExtendsClass:[$it]" } ?: "",
            isNoImplementsClass?.let { "isNoImplementsClass:[$it]" } ?: "",
            extendsClass.takeIf { it.isNotEmpty() }?.let { "extendsClass:$it" } ?: "",
            implementsClass.takeIf { it.isNotEmpty() }?.let { "implementsClass:$it" } ?: "",
            enclosingClass.takeIf { it.isNotEmpty() }?.let { "enclosingClass:$it" } ?: "",
            memberRules.takeIf { it.isNotEmpty() }?.let { "memberRules:[${it.size} existed]" } ?: "",
            fieldRules.takeIf { it.isNotEmpty() }?.let { "fieldRules:[${it.size} existed]" } ?: "",
            methodRules.takeIf { it.isNotEmpty() }?.let { "methodRules:[${it.size} existed]" } ?: "",
            constroctorRules.takeIf { it.isNotEmpty() }?.let { "constroctorRules:[${it.size} existed]" } ?: ""
        )

    override val objectName get() = "Class"

    override val isInitialize
        get() = super.isInitialize || fromPackages.isNotEmpty() || fullName != null || simpleName != null || singleName != null ||
                fullNameConditions != null || simpleNameConditions != null || singleNameConditions != null || isAnonymousClass != null ||
                isNoExtendsClass != null || isNoImplementsClass != null || extendsClass.isNotEmpty() || enclosingClass.isNotEmpty() ||
                memberRules.isNotEmpty() || fieldRules.isNotEmpty() || methodRules.isNotEmpty() || constroctorRules.isNotEmpty()

    override fun hashCode(other: Any?) = super.hashCode(other) + toString().hashCode()

    override fun toString() = "[$fromPackages][$fullName][$simpleName][$singleName][$fullNameConditions][$simpleNameConditions]" +
            "[$singleNameConditions][$modifiers][$isAnonymousClass][$isNoExtendsClass][$isNoImplementsClass][$extendsClass][$implementsClass]" +
            "[$enclosingClass][$memberRules][$fieldRules][$methodRules][$constroctorRules]"
}
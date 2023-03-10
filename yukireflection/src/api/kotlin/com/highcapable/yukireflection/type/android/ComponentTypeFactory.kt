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
 * This file is Created by fankes on 2022/2/2.
 * This file is Modified by fankes on 2023/1/21.
 */
@file:Suppress("unused", "KDocUnresolvedReference", "DEPRECATION")

package com.highcapable.yukireflection.type.android

import android.app.*
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.*
import android.content.Intent.ShortcutIconResource
import android.content.pm.*
import android.content.pm.LauncherApps.ShortcutQuery
import android.content.res.*
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.*
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.*
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.*
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.highcapable.yukireflection.factory.classOf
import com.highcapable.yukireflection.factory.toClass
import com.highcapable.yukireflection.factory.toClassOrNull

/**
 * ?????? [android.R] ??????
 * @return [Class]<[android.R]>
 */
val AndroidRClass get() = classOf<android.R>()

/**
 * ?????? [Context] ??????
 * @return [Class]<[Context]>
 */
val ContextClass get() = classOf<Context>()

/**
 * ?????? [ContextImpl] ??????
 * @return [Class]
 */
val ContextImplClass get() = "android.app.ContextImpl".toClass()

/**
 * ?????? [ContextWrapper] ??????
 * @return [Class]<[ContextWrapper]>
 */
val ContextWrapperClass get() = classOf<ContextWrapper>()

/**
 * ?????? [Application] ??????
 * @return [Class]<[Application]>
 */
val ApplicationClass get() = classOf<Application>()

/**
 * ?????? [ApplicationInfo] ??????
 * @return [Class]<[ApplicationInfo]>
 */
val ApplicationInfoClass get() = classOf<ApplicationInfo>()

/**
 * ?????? [Instrumentation] ??????
 * @return [Class]<[Instrumentation]>
 */
val InstrumentationClass get() = classOf<Instrumentation>()

/**
 * ?????? [PackageInfo] ??????
 * @return [Class]<[PackageInfo]>
 */
val PackageInfoClass get() = classOf<PackageInfo>()

/**
 * ?????? [ApplicationPackageManager] ??????
 * @return [Class]
 */
val ApplicationPackageManagerClass get() = "android.app.ApplicationPackageManager".toClass()

/**
 * ?????? [ActivityThread] ??????
 * @return [Class]
 */
val ActivityThreadClass get() = "android.app.ActivityThread".toClass()

/**
 * ?????? [ActivityManager] ??????
 * @return [Class]<[ActivityManager]>
 */
val ActivityManagerClass get() = classOf<ActivityManager>()

/**
 * ?????? [IActivityManager] ??????
 * @return [Class]
 */
val IActivityManagerClass get() = "android.app.IActivityManager".toClass()

/**
 * ?????? [ActivityManagerNative] ??????
 * @return [Class]
 */
val ActivityManagerNativeClass get() = "android.app.ActivityManagerNative".toClass()

/**
 * ?????? [IActivityTaskManager] ??????
 *
 * - ?????? Android O (26) ?????????????????????
 * @return [Class] or null
 */
val IActivityTaskManagerClass get() = "android.app.IActivityTaskManager".toClassOrNull()

/**
 * ?????? [ActivityTaskManager] ??????
 *
 * - ?????? Android O (26) ?????????????????????
 * @return [Class] or null
 */
val ActivityTaskManagerClass get() = "android.app.ActivityTaskManager".toClassOrNull()

/**
 * ?????? [IPackageManager] ??????
 * @return [Class]
 */
val IPackageManagerClass get() = "android.content.pm.IPackageManager".toClass()

/**
 * ?????? [ClientTransaction] ??????
 * @return [Class]
 */
val ClientTransactionClass get() = "android.app.servertransaction.ClientTransaction".toClass()

/**
 * ?????? [LoadedApk] ??????
 * @return [Class]
 */
val LoadedApkClass get() = "android.app.LoadedApk".toClass()

/**
 * ?????? [Singleton] ??????
 * @return [Class]
 */
val SingletonClass get() = "android.util.Singleton".toClass()

/**
 * ?????? [Activity] ??????
 * @return [Class]<[Activity]>
 */
val ActivityClass get() = classOf<Activity>()

/**
 * ?????? [Looper] ??????
 * @return [Class]<[Looper]>
 */
val LooperClass get() = classOf<Looper>()

/**
 * ?????? [Fragment] ?????? - Support
 * @return [Class]
 */
val FragmentClass_AndroidSupport get() = "android.support.v4.app.Fragment".toClass()

/**
 * ?????? [Fragment] ?????? - AndroidX
 * @return [Class]
 */
val FragmentClass_AndroidX get() = "androidx.fragment.app.Fragment".toClass()

/**
 * ?????? [FragmentActivity] ?????? - Support
 * @return [Class]
 */
val FragmentActivityClass_AndroidSupport get() = "android.support.v4.app.FragmentActivity".toClass()

/**
 * ?????? [FragmentActivity] ?????? - AndroidX
 * @return [Class]
 */
val FragmentActivityClass_AndroidX get() = "androidx.fragment.app.FragmentActivity".toClass()

/**
 * ?????? [DocumentFile] ?????? - AndroidX
 * @return [Class]
 */
val DocumentFileClass get() = "androidx.documentfile.provider.DocumentFile".toClass()

/**
 * ?????? [Service] ??????
 * @return [Class]<[Service]>
 */
val ServiceClass get() = classOf<Service>()

/**
 * ?????? [Binder] ??????
 * @return [Class]<[Binder]>
 */
val BinderClass get() = classOf<Binder>()

/**
 * ?????? [IBinder] ??????
 * @return [Class]<[IBinder]>
 */
val IBinderClass get() = classOf<IBinder>()

/**
 * ?????? [BroadcastReceiver] ??????
 * @return [Class]<[BroadcastReceiver]>
 */
val BroadcastReceiverClass get() = classOf<BroadcastReceiver>()

/**
 * ?????? [Bundle] ??????
 * @return [Class]<[Bundle]>
 */
val BundleClass get() = classOf<Bundle>()

/**
 * ?????? [BaseBundle] ??????
 * @return [Class]<[BaseBundle]>
 */
val BaseBundleClass get() = classOf<BaseBundle>()

/**
 * ?????? [Resources] ??????
 * @return [Class]<[Resources]>
 */
val ResourcesClass get() = classOf<Resources>()

/**
 * ?????? [Configuration] ??????
 * @return [Class]<[Configuration]>
 */
val ConfigurationClass get() = classOf<Configuration>()

/**
 * ?????? [ConfigurationInfo] ??????
 * @return [Class]<[ConfigurationInfo]>
 */
val ConfigurationInfoClass get() = classOf<ConfigurationInfo>()

/**
 * ?????? [ContentResolver] ??????
 * @return [Class]<[ContentResolver]>
 */
val ContentResolverClass get() = classOf<ContentResolver>()

/**
 * ?????? [ContentProvider] ??????
 * @return [Class]<[ContentProvider]>
 */
val ContentProviderClass get() = classOf<ContentProvider>()

/**
 * ?????? [Settings] ??????
 * @return [Class]<[Settings]>
 */
val SettingsClass get() = classOf<Settings>()

/**
 * ?????? [Settings.System] ??????
 * @return [Class]<[Settings.System]>
 */
val Settings_SystemClass get() = classOf<Settings.System>()

/**
 * ?????? [Settings.Secure] ??????
 * @return [Class]<[Settings.Secure]>
 */
val Settings_SecureClass get() = classOf<Settings.Secure>()

/**
 * ?????? [TypedArray] ??????
 * @return [Class]<[TypedArray]>
 */
val TypedArrayClass get() = classOf<TypedArray>()

/**
 * ?????? [TypedValue] ??????
 * @return [Class]<[TypedValue]>
 */
val TypedValueClass get() = classOf<TypedValue>()

/**
 * ?????? [SparseArray] ??????
 * @return [Class]<[SparseArray]>
 */
val SparseArrayClass get() = classOf<SparseArray<*>>()

/**
 * ?????? [SparseIntArray] ??????
 * @return [Class]<[SparseIntArray]>
 */
val SparseIntArrayClass get() = classOf<SparseIntArray>()

/**
 * ?????? [SparseBooleanArray] ??????
 * @return [Class]<[SparseBooleanArray]>
 */
val SparseBooleanArrayClass get() = classOf<SparseBooleanArray>()

/**
 * ?????? [SparseLongArray] ??????
 * @return [Class]<[SparseLongArray]>
 */
val SparseLongArrayClass get() = classOf<SparseLongArray>()

/**
 * ?????? [LongSparseArray] ??????
 * @return [Class]<[LongSparseArray]>
 */
val LongSparseArrayClass get() = classOf<LongSparseArray<*>>()

/**
 * ?????? [ArrayMap] ??????
 * @return [Class]<[ArrayMap]>
 */
val ArrayMapClass get() = classOf<ArrayMap<*, *>>()

/**
 * ?????? [ArraySet] ??????
 *
 * - ?????? Android M (23) ?????????????????????
 * @return [Class]<[ArraySet]> or null
 */
val ArraySetClass get() = if (Build.VERSION.SDK_INT >= 23) classOf<ArraySet<*>>() else null

/**
 * ?????? [Handler] ??????
 * @return [Class]<[Handler]>
 */
val HandlerClass get() = classOf<Handler>()

/**
 * ?????? [Handler.Callback] ??????
 * @return [Class]<[Handler.Callback]>
 */
val Handler_CallbackClass get() = classOf<Handler.Callback>()

/**
 * ?????? [Message] ??????
 * @return [Class]<[Message]>
 */
val MessageClass get() = classOf<Message>()

/**
 * ?????? [MessageQueue] ??????
 * @return [Class]<[MessageQueue]>
 */
val MessageQueueClass get() = classOf<MessageQueue>()

/**
 * ?????? [Messenger] ??????
 * @return [Class]<[Messenger]>
 */
val MessengerClass get() = classOf<Messenger>()

/**
 * ?????? [AsyncTask] ??????
 * @return [Class]<[AsyncTask]>
 */
val AsyncTaskClass get() = classOf<AsyncTask<*, *, *>>()

/**
 * ?????? [SimpleDateFormat] ??????
 *
 * - ?????? Android N (24) ?????????????????????
 * @return [Class]<[SimpleDateFormat]> or null
 */
val SimpleDateFormatClass_Android get() = if (Build.VERSION.SDK_INT >= 24) classOf<SimpleDateFormat>() else null

/**
 * ?????? [Base64] ??????
 * @return [Class]<[Base64]>
 */
val Base64Class_Android get() = classOf<Base64>()

/**
 * ?????? [Window] ??????
 * @return [Class]<[Window]>
 */
val WindowClass get() = classOf<Window>()

/**
 * ?????? [WindowMetrics] ??????
 *
 * - ?????? Android R (30) ?????????????????????
 * @return [Class]<[WindowMetrics]> or null
 */
val WindowMetricsClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowMetrics>() else null

/**
 * ?????? [WindowInsets] ??????
 * @return [Class]<[WindowInsets]>
 */
val WindowInsetsClass get() = classOf<WindowInsets>()

/**
 * ?????? [WindowInsets.Type] ??????
 *
 * - ?????? Android R (30) ?????????????????????
 * @return [Class]<[WindowInsets.Type]> or null
 */
val WindowInsets_TypeClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowInsets.Type>() else null

/**
 * ?????? [WindowManager] ??????
 * @return [Class]<[WindowManager]>
 */
val WindowManagerClass get() = classOf<WindowManager>()

/**
 * ?????? [WindowManager.LayoutParams] ??????
 * @return [Class]<[WindowManager.LayoutParams]>
 */
val WindowManager_LayoutParamsClass get() = classOf<WindowManager.LayoutParams>()

/**
 * ?????? [ViewManager] ??????
 * @return [Class]<[ViewManager]>
 */
val ViewManagerClass get() = classOf<ViewManager>()

/**
 * ?????? [Parcel] ??????
 * @return [Class]<[Parcel]>
 */
val ParcelClass get() = classOf<Parcel>()

/**
 * ?????? [Parcelable] ??????
 * @return [Class]<[Parcelable]>
 */
val ParcelableClass get() = classOf<Parcelable>()

/**
 * ?????? [Parcelable.Creator] ??????
 * @return [Class]<[Parcelable.Creator]>
 */
val Parcelable_CreatorClass get() = classOf<Parcelable.Creator<*>>()

/**
 * ?????? [Dialog] ??????
 * @return [Class]<[Dialog]>
 */
val DialogClass get() = classOf<Dialog>()

/**
 * ?????? [AlertDialog] ??????
 * @return [Class]<[AlertDialog]>
 */
val AlertDialogClass get() = classOf<AlertDialog>()

/**
 * ?????? [DisplayMetrics] ??????
 * @return [Class]<[DisplayMetrics]>
 */
val DisplayMetricsClass get() = classOf<DisplayMetrics>()

/**
 * ?????? [Display] ??????
 * @return [Class]<[Display]>
 */
val DisplayClass get() = classOf<Display>()

/**
 * ?????? [Toast] ??????
 * @return [Class]<[Toast]>
 */
val ToastClass get() = classOf<Toast>()

/**
 * ?????? [Intent] ??????
 * @return [Class]<[Intent]>
 */
val IntentClass get() = classOf<Intent>()

/**
 * ?????? [ComponentInfo] ??????
 * @return [Class]<[ComponentInfo]>
 */
val ComponentInfoClass get() = classOf<ComponentInfo>()

/**
 * ?????? [ComponentName] ??????
 * @return [Class]<[ComponentName]>
 */
val ComponentNameClass get() = classOf<ComponentName>()

/**
 * ?????? [PendingIntent] ??????
 * @return [Class]<[PendingIntent]>
 */
val PendingIntentClass get() = classOf<PendingIntent>()

/**
 * ?????? [ColorStateList] ??????
 * @return [Class]<[ColorStateList]>
 */
val ColorStateListClass get() = classOf<ColorStateList>()

/**
 * ?????? [ContentValues] ??????
 * @return [Class]<[ContentValues]>
 */
val ContentValuesClass get() = classOf<ContentValues>()

/**
 * ?????? [SharedPreferences] ??????
 * @return [Class]<[SharedPreferences]>
 */
val SharedPreferencesClass get() = classOf<SharedPreferences>()

/**
 * ?????? [MediaPlayer] ??????
 * @return [Class]<[MediaPlayer]>
 */
val MediaPlayerClass get() = classOf<MediaPlayer>()

/**
 * ?????? [ProgressDialog] ??????
 * @return [Class]<[ProgressDialog]>
 */
val ProgressDialogClass get() = classOf<ProgressDialog>()

/**
 * ?????? [Log] ??????
 * @return [Class]<[Log]>
 */
val LogClass get() = classOf<Log>()

/**
 * ?????? [Build] ??????
 * @return [Class]<[Build]>
 */
val BuildClass get() = classOf<Build>()

/**
 * ?????? [Xml] ??????
 * @return [Class]<[Xml]>
 */
val XmlClass get() = classOf<Xml>()

/**
 * ?????? [ContrastColorUtil] ??????
 * @return [Class]
 */
val ContrastColorUtilClass get() = "com.android.internal.util.ContrastColorUtil".toClass()

/**
 * ?????? [StatusBarNotification] ??????
 * @return [Class]<[StatusBarNotification]>
 */
val StatusBarNotificationClass get() = classOf<StatusBarNotification>()

/**
 * ?????? [Notification] ??????
 * @return [Class]<[Notification]>
 */
val NotificationClass get() = classOf<Notification>()

/**
 * ?????? [Notification.Builder] ??????
 * @return [Class]<[Notification.Builder]>
 */
val Notification_BuilderClass get() = classOf<Notification.Builder>()

/**
 * ?????? [Notification.Action] ??????
 * @return [Class]<[Notification.Action]>
 */
val Notification_ActionClass get() = classOf<Notification.Action>()

/**
 * ?????? [DialogInterface] ??????
 * @return [Class]<[DialogInterface]>
 */
val DialogInterfaceClass get() = classOf<DialogInterface>()

/**
 * ?????? [DialogInterface.OnClickListener] ??????
 * @return [Class]<[DialogInterface.OnClickListener]>
 */
val DialogInterface_OnClickListenerClass get() = classOf<DialogInterface.OnClickListener>()

/**
 * ?????? [DialogInterface.OnCancelListener] ??????
 * @return [Class]<[DialogInterface.OnCancelListener]>
 */
val DialogInterface_OnCancelListenerClass get() = classOf<DialogInterface.OnCancelListener>()

/**
 * ?????? [DialogInterface.OnDismissListener] ??????
 * @return [Class]<[DialogInterface.OnDismissListener]>
 */
val DialogInterface_OnDismissListenerClass get() = classOf<DialogInterface.OnDismissListener>()

/**
 * ?????? [Environment] ??????
 * @return [Class]<[Environment]>
 */
val EnvironmentClass get() = classOf<Environment>()

/**
 * ?????? [Process] ??????
 * @return [Class]<[Process]>
 */
val ProcessClass get() = classOf<Process>()

/**
 * ?????? [Vibrator] ??????
 * @return [Class]<[Vibrator]>
 */
val VibratorClass get() = classOf<Vibrator>()

/**
 * ?????? [VibrationEffect] ??????
 *
 * - ?????? Android O (26) ?????????????????????
 * @return [Class]<[VibrationEffect]> or null
 */
val VibrationEffectClass get() = if (Build.VERSION.SDK_INT >= 26) classOf<VibrationEffect>() else null

/**
 * ?????? [VibrationAttributes] ??????
 *
 * - ?????? Android R (30) ?????????????????????
 * @return [Class]<[VibrationAttributes]> or null
 */
val VibrationAttributesClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<VibrationAttributes>() else null

/**
 * ?????? [SystemClock] ??????
 * @return [Class]<[SystemClock]>
 */
val SystemClockClass get() = classOf<SystemClock>()

/**
 * ?????? [PowerManager] ??????
 * @return [Class]<[PowerManager]>
 */
val PowerManagerClass get() = classOf<PowerManager>()

/**
 * ?????? [PowerManager.WakeLock] ??????
 * @return [Class]<[PowerManager.WakeLock]>
 */
val PowerManager_WakeLockClass get() = classOf<PowerManager.WakeLock>()

/**
 * ?????? [UserHandle] ??????
 * @return [Class]<[UserHandle]>
 */
val UserHandleClass get() = classOf<UserHandle>()

/**
 * ?????? [ShortcutInfo] ??????
 *
 * - ?????? Android N_MR1 (25) ?????????????????????
 * @return [Class]<[ShortcutInfo]> or null
 */
val ShortcutInfoClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutInfo>() else null

/**
 * ?????? [ShortcutManager] ??????
 *
 * - ?????? Android R (30) ?????????????????????
 * @return [Class]<[ShortcutManager]> or null
 */
val ShortcutManagerClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<ShortcutManager>() else null

/**
 * ?????? [ShortcutQuery] ??????
 *
 * - ?????? Android N_MR1 (25) ?????????????????????
 * @return [Class]<[ShortcutQuery]> or null
 */
val ShortcutQueryClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutQuery>() else null

/**
 * ?????? [KeyboardShortcutInfo] ??????
 * @return [Class]<[KeyboardShortcutInfo]>
 */
val KeyboardShortcutInfoClass get() = classOf<KeyboardShortcutInfo>()

/**
 * ?????? [KeyboardShortcutGroup] ??????
 * @return [Class]<[KeyboardShortcutGroup]>
 */
val KeyboardShortcutGroupClass get() = classOf<KeyboardShortcutGroup>()

/**
 * ?????? [ShortcutIconResource] ??????
 * @return [Class]<[ShortcutIconResource]>
 */
val ShortcutIconResourceClass get() = classOf<ShortcutIconResource>()

/**
 * ?????? [AssetManager] ??????
 * @return [Class]<[AssetManager]>
 */
val AssetManagerClass get() = classOf<AssetManager>()

/**
 * ?????? [AppWidgetManager] ??????
 * @return [Class]<[AppWidgetManager]>
 */
val AppWidgetManagerClass get() = classOf<AppWidgetManager>()

/**
 * ?????? [AppWidgetProvider] ??????
 * @return [Class]<[AppWidgetProvider]>
 */
val AppWidgetProviderClass get() = classOf<AppWidgetProvider>()

/**
 * ?????? [AppWidgetProviderInfo] ??????
 * @return [Class]<[AppWidgetProviderInfo]>
 */
val AppWidgetProviderInfoClass get() = classOf<AppWidgetProviderInfo>()

/**
 * ?????? [AppWidgetHost] ??????
 * @return [Class]<[AppWidgetHost]>
 */
val AppWidgetHostClass get() = classOf<AppWidgetHost>()

/**
 * ?????? [ActivityInfo] ??????
 * @return [Class]<[ActivityInfo]>
 */
val ActivityInfoClass get() = classOf<ActivityInfo>()

/**
 * ?????? [ResolveInfo] ??????
 * @return [Class]<[ResolveInfo]>
 */
val ResolveInfoClass get() = classOf<ResolveInfo>()

/**
 * ?????? [Property] ??????
 * @return [Class]<[Property]>
 */
val PropertyClass get() = classOf<Property<*, *>>()

/**
 * ?????? [IntProperty] ??????
 * @return [Class]<[IntProperty]>
 */
val IntPropertyClass get() = classOf<IntProperty<*>>()

/**
 * ?????? [FloatProperty] ??????
 * @return [Class]<[FloatProperty]>
 */
val FloatPropertyClass get() = classOf<FloatProperty<*>>()

/**
 * ?????? [SQLiteDatabase] ??????
 * @return [Class]<[SQLiteDatabase]>
 */
val SQLiteDatabaseClass get() = classOf<SQLiteDatabase>()

/**
 * ?????? [StrictMode] ??????
 * @return [Class]<[StrictMode]>
 */
val StrictModeClass get() = classOf<StrictMode>()

/**
 * ?????? [AccessibilityManager] ??????
 * @return [Class]<[AccessibilityManager]>
 */
val AccessibilityManagerClass get() = classOf<AccessibilityManager>()

/**
 * ?????? [AccessibilityEvent] ??????
 * @return [Class]<[AccessibilityEvent]>
 */
val AccessibilityEventClass get() = classOf<AccessibilityEvent>()

/**
 * ?????? [AccessibilityNodeInfo] ??????
 * @return [Class]<[AccessibilityNodeInfo]>
 */
val AccessibilityNodeInfoClass get() = classOf<AccessibilityNodeInfo>()

/**
 * ?????? [IInterface] ??????
 * @return [Class]<[IInterface]>
 */
val IInterfaceClass get() = classOf<IInterface>()
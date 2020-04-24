//package com.example.coronamap.permission
//
//import android.content.Context
//import com.gun0912.tedpermission.PermissionListener
//import com.gun0912.tedpermission.TedPermission
//import java.util.ArrayList
//
//class Permission (context1: Context) {
//    var context: Context = context1
//
//    var permissionListener: PermissionListener = object : PermissionListener {
//        override fun onPermissionGranted() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }
//    fun checkPermission() {
//        TedPermission.with(context)
//                .setPermissionListener(permissionListener)
//                .setRationaleMessage("앱의 기능을 사용하기 위해서는 권한이 필요합니다.")
//                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용 할 수 있습니다.")
//                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                .check()
//    }
//}
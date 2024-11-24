package eu.mcomputng.mobv.zadanie.fragments.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.databinding.FragmentPhotoEditorBinding
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel
import kotlinx.coroutines.launch


class PhotoEditorFragment : Fragment(R.layout.fragment_photo_editor) {

    private lateinit var viewModelProfile: ProfileViewModel
    private var binding: FragmentPhotoEditorBinding? = null

    fun REQUIRED_PERMISSIONS(): Array<String> {
        return if (Build.VERSION.SDK_INT < 33) {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).toTypedArray()
        } else if (Build.VERSION.SDK_INT == 33) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            ).toTypedArray()
        } else {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ).toTypedArray()
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val deniedPermissions = permissions.filterValues { !it }.keys
            if (deniedPermissions.isNotEmpty()) {
                // At least one permission is denied
                viewModelProfile.galleryPermissionsGranted.postValue(false)
                Toast.makeText(
                    requireContext(),
                    "Nie je povolený prístup k médiám",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                viewModelProfile.galleryPermissionsGranted.postValue(true)
                Log.d("permisssions", "granted")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelProfile = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding = FragmentPhotoEditorBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.selectPhotoIcon.setOnClickListener{
                Log.d("select", "click")

                /*if (checkPermissions(requireContext(),true)) {
                    openGallery()
                }else{
                    Log.d("Persmissions", "false")
                }*/
                checkPermissions(requireContext())
                if (viewModelProfile.galleryPermissionsGranted.value == true){
                    openGallery()
                }
            }

            bnd.changePhotoBtn.setOnClickListener{
                Log.d("change", "click")
            }

            bnd.deletePhotoBtn.setOnClickListener {
                Log.d("delete", "click")
            }

            bnd.backArrow.setOnClickListener{
                findNavController().popBackStack()
            }

            /*viewModelProfile.galleryPermissionsGranted.observe(viewLifecycleOwner) {
                it?.let {
                    if (it){
                        Toast.makeText(
                            requireContext(),
                            "Je povolený prístup k médiám",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }*/

            viewModelProfile.userResult.observe(viewLifecycleOwner) {result ->
                result?.let {
                    if (it.user?.photo?.isEmpty() == true){
                        bnd.photoEditorNoPhoto.visibility = View.VISIBLE
                        bnd.selectPhotoIcon.visibility = View.VISIBLE
                        bnd.photoEditorPreview.visibility = View.GONE
                        bnd.changePhotoBtn.visibility = View.GONE
                        bnd.deletePhotoBtn.visibility = View.GONE
                    }else{
                        bnd.photoEditorNoPhoto.visibility = View.GONE
                        bnd.selectPhotoIcon.visibility = View.GONE
                        bnd.photoEditorPreview.visibility = View.VISIBLE
                        bnd.changePhotoBtn.visibility = View.VISIBLE
                        bnd.deletePhotoBtn.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

   val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            //changePhoto(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    fun openGallery() {
        lifecycleScope.launch {
            val mimeType = "image/jpeg"
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.SingleMimeType(
                        mimeType
                    )
                )
            )
        }
    }


    fun checkPermissions(context: Context) {
        //val check = allPermissionsGranted(context)
        //Log.d("povolenia", "je $check  ask $ask")
        if (!allPermissionsGranted(context)) {
            Log.d("permisssions", "checking")
            //some permissions are not granted
            requestPermissionLauncher.launch(
                REQUIRED_PERMISSIONS()
            )
        }else{
            Log.d("permisssions", "already allowed")
            viewModelProfile.galleryPermissionsGranted.postValue(true)
        }
        //return check
    }

    fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS().all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
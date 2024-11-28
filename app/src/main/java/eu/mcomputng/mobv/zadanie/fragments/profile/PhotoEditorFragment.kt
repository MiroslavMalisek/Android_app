package eu.mcomputng.mobv.zadanie.fragments.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import eu.mcomputng.mobv.zadanie.Utils.displayPhotoFromUri
import eu.mcomputng.mobv.zadanie.Utils.displayPhotoFromFile
import eu.mcomputng.mobv.zadanie.Utils.PHOTOBASEURI


class PhotoEditorFragment : Fragment(R.layout.fragment_photo_editor) {

    private lateinit var viewModelProfile: ProfileViewModel
    private var selectedPhoto: File? = null
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

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Handle the back button press
            viewModelProfile.clearPhotoEditor()
            findNavController().popBackStack()
        }

        binding = FragmentPhotoEditorBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.selectPhotoIcon.setOnClickListener{
                checkPermissions(requireContext())
                if (viewModelProfile.galleryPermissionsGranted.value == true){
                    openGallery()
                }
            }

            bnd.changeSelectedPhotoBtn.setOnClickListener{
                checkPermissions(requireContext())
                if (viewModelProfile.galleryPermissionsGranted.value == true){
                    openGallery()
                }
            }

            bnd.changeProfilePhotoBtn.setOnClickListener {
                checkPermissions(requireContext())
                if (viewModelProfile.galleryPermissionsGranted.value == true){
                    openGallery()
                }
            }

            bnd.confirmPhotoBtn.setOnClickListener {
                val photo = this.selectedPhoto
                if (photo != null){
                    viewModelProfile.uploadProfilePhoto(requireContext(), photo)
                }else{
                    Snackbar.make(requireView(), "Nebol vybraný žiadny súbor", Snackbar.LENGTH_LONG).show()
                }
            }

            bnd.deletePhotoBtn.setOnClickListener {
                Log.d("delete", "click")
                if (viewModelProfile.userResult.value?.user?.photo?.isNotEmpty() == true){
                    viewModelProfile.deleteProfilePhoto(requireContext())
                }else{
                    Snackbar.make(requireView(), "Nie je nastavená profilová fotka", Snackbar.LENGTH_LONG).show()
                }
            }

            bnd.backArrow.setOnClickListener{
                viewModelProfile.clearPhotoEditor()
                findNavController().popBackStack()
            }

            viewModelProfile.uploadProfilePhotoResult.observe(viewLifecycleOwner){
                if (it.message.isNotEmpty()) {
                    Log.d("upload", it.message)
                    Snackbar.make(requireView(), it.message, Snackbar.LENGTH_LONG).show()
                }
            }

            viewModelProfile.deleteProfilePhotoResult.observe(viewLifecycleOwner){
                if (it.message.isNotEmpty()) {
                    Log.d("delete", it.message)
                    Snackbar.make(requireView(), it.message, Snackbar.LENGTH_LONG).show()
                }
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
                    //if local user doesnt have profile photo - show gallery selector
                    if (it.user?.photo?.isEmpty() == true){
                        bnd.photoEditorNoPhoto.visibility = View.VISIBLE
                        bnd.selectPhotoIcon.visibility = View.VISIBLE
                        bnd.photoEditorPreview.visibility = View.GONE
                        bnd.confirmPhotoBtn.visibility = View.GONE
                        bnd.changeSelectedPhotoBtn.visibility = View.GONE
                        bnd.changeProfilePhotoBtn.visibility = View.GONE
                        bnd.deletePhotoBtn.visibility = View.GONE
                    }else{
                        //if has photo, show it and allow change and delete
                        val userPhoto = it.user?.photo
                        Log.d("path", PHOTOBASEURI+userPhoto)
                        displayPhotoFromUri(bnd.photoEditorPreview, PHOTOBASEURI + userPhoto)
                        bnd.photoEditorNoPhoto.visibility = View.GONE
                        bnd.selectPhotoIcon.visibility = View.GONE
                        bnd.confirmPhotoBtn.visibility = View.GONE
                        bnd.changeSelectedPhotoBtn.visibility = View.GONE
                        bnd.photoEditorPreview.visibility = View.VISIBLE
                        bnd.changeProfilePhotoBtn.visibility = View.VISIBLE
                        bnd.deletePhotoBtn.visibility = View.VISIBLE
                    }
                }
            }

            /*viewModelProfile.photoSelected.observe(viewLifecycleOwner){selectedPhoto->
                selectedPhoto?.let {
                    Log.d("change photo", selectedPhoto.absolutePath)
                    this.displayPhoto(selectedPhoto)
                    bnd.photoEditorNoPhoto.visibility = View.GONE
                    bnd.selectPhotoIcon.visibility = View.GONE
                    bnd.photoEditorPreview.visibility = View.VISIBLE
                    bnd.changePhotoBtn.visibility = View.VISIBLE
                    bnd.confirmPhotoBtn.visibility = View.VISIBLE
                    bnd.deletePhotoBtn.visibility = View.GONE
                }
            }*/
        }
    }

    fun FragmentPhotoEditorBinding.showPreview(selectedPhoto: File) {
        displayPhotoFromFile(photoEditorPreview, selectedPhoto)
        photoEditorNoPhoto.visibility = View.GONE
        selectPhotoIcon.visibility = View.GONE
        changeProfilePhotoBtn.visibility = View.GONE
        deletePhotoBtn.visibility = View.GONE
        photoEditorPreview.visibility = View.VISIBLE
        changeSelectedPhotoBtn.visibility = View.VISIBLE
        confirmPhotoBtn.visibility = View.VISIBLE
    }

   val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            changePhoto(uri)?.let {
                //chosen photo successfully
                this.selectedPhoto = it
                binding?.showPreview(it)
            } ?: run {
                Snackbar.make(requireView(), "Súbor sa nepodarilo načítať", Snackbar.LENGTH_LONG).show()
            }
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

    fun inputStreamToFile(
        uri: Uri,
    ): File? {
        val resolver = requireContext().applicationContext.contentResolver
        resolver.openInputStream(uri).use { inputStream ->
            var orig = File(requireContext().filesDir, "photo_copied.jpg")
            if (orig.exists()) {
                orig.delete()
            }
            orig = File(requireContext().filesDir, "photo_copied.jpg")

            FileOutputStream(orig).use { fileOutputStream ->
                if (inputStream == null) {
                    Log.d("vybrane", "stream null")
                    return null
                }
                try {
                    Log.d("vybrane", "copied")
                    inputStream.copyTo(fileOutputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                }
            }
            Log.d("vybrane", orig.absolutePath)
            return orig
        }

    }

    private fun changePhoto(file: Uri): File? {
        inputStreamToFile(file)?.let {
            Log.d("vybrane", "vybrane je $it")
            return it
        }
        return null
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
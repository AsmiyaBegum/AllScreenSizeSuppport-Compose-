package com.ab.screensizesupport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ab.screensizesupport.composables.WindowInfo
import com.ab.screensizesupport.composables.rememberWindowInfo
import com.ab.screensizesupport.model.VideoResultEntity
import com.ab.screensizesupport.ui.theme.AllScreenSizeSupportJetpackComposeTheme
import com.ab.screensizesupport.utubevideo.VideoPlayList
import com.ab.screensizesupport.utubevideo.VideoPlayer


class MainActivity : ComponentActivity() {

    val videoList : List<VideoResultEntity> = listOf(

        VideoResultEntity("1","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","BigBuckBunny",""),
        VideoResultEntity("2","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4","ElephantsDream",""),
        VideoResultEntity("3","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4","ForBiggerBlazes",""),
        VideoResultEntity("4","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4","ForBiggerEscapes",""),
        VideoResultEntity("5","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4","ForBiggerFun","")


    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AllScreenSizeSupportJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    VideoPlayingScreen(videos = videoList )
                }
            }
        }
    }
}

@Composable
fun VideoPlayingScreen(videos : List<VideoResultEntity>){
    val windowInfo =  rememberWindowInfo()
    val playingIndex = remember { mutableStateOf(0) }

    fun onVideoChange(index : Int){
        playingIndex.value = index
    }
    if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            VideoPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true),
                videos = videos,
                currentPlaying = playingIndex.value,
                onVideoChange =  { index ->
                    onVideoChange(index)
                }

            )

            VideoPlayList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true),
                videos = videos,
                currentPlaying = playingIndex.value,
                onVideoChange = { index ->
                    onVideoChange(index)
                }

            )
        }
    }else{
        Row (
            modifier = Modifier.fillMaxSize()
        ){
            VideoPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true),
                videos = videos,
                currentPlaying = playingIndex.value,
                onVideoChange =  { index ->
                    onVideoChange(index)
                }

            )

            VideoPlayList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true),
                videos = videos,
                currentPlaying = playingIndex.value,
                onVideoChange = { index ->
                    onVideoChange(index)
                })

        }
    }

}



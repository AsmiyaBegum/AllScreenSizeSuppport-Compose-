package com.ab.screensizesupport.utubevideo

import android.media.Image
import android.media.MediaMetadata
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SimpleAdapter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import com.ab.screensizesupport.R
import com.ab.screensizesupport.model.VideoResultEntity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent


/*
 Build simple exoplayer and set the media item.
 Prepare for media sources
 */


//@Composable
//fun VideoPlayer(modifier: Modifier) {
//    val context = LocalContext.current
//
//    // create simple exoplayer
//
//    val exoplayer = remember {
//        SimpleExoPlayer.Builder(context).build().apply {
//            this.prepare()
//        }
//    }
//
//    ConstraintLayout(modifier = modifier) {
//        val(title,videoPlayer) = createRefs()
//
//        // Video title
//
//        Text(
//            text  = "Video Title",
//            color = Color.White,
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .constrainAs(title) {
//                    top.linkTo(parent.top)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//        )
//
//        // Player View
//
//        DisposableEffect(
//            AndroidView(
//                modifier = Modifier
//                    .testTag("VideoPlayer")
//                    .constrainAs(videoPlayer) {
//                        top.linkTo(parent.top)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                        bottom.linkTo(parent.bottom)
//                    },
//            factory = {
//                    // ExoplayerView for video player
//                PlayerView(context).apply {
//                    player = exoplayer
//                    layoutParams =
//                        FrameLayout.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                        )
//                }
//                })){
//                onDispose {
//
//                    // release exoplayer when no longer needed
//
//                    exoplayer.release()
//                }
//            }
//
//    }
//
//
//}




@Composable
fun VideoPlayer(
    modifier : Modifier,
    videos : List<VideoResultEntity>,
    currentPlaying : Int,
    onVideoChange : (Int) -> Unit
){

    val context = LocalContext.current
    val mediaItems = arrayListOf<MediaItem>()

    val visibleState = remember {
        mutableStateOf(true)
    }

    val videoTitle = remember{
        mutableStateOf(videos[currentPlaying].name)
    }

    // create MediaItem

    videos.forEach{
        mediaItems.add(
            MediaItem.Builder()
                .setUri(it.videoUri)
                .setMediaId(it.id)
                .setTag(it)
                .setMediaMetadata(
                    com.google.android.exoplayer2.MediaMetadata.Builder()
                        .setDisplayTitle(it.name)
                        .build()
                ).build()
        )
    }

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            this.setMediaItems(mediaItems)
            this.prepare()
            this.playWhenReady = true

            // everytime an item in playlist is clicked play that video
            this.seekTo(currentPlaying, C.TIME_UNSET)

            addListener(
                object : Player.Listener{
                    override fun onEvents(player: Player, events: Player.Events) {
                        super.onEvents(player, events)

                        // hide the title when player duration is atleast 200
                        if(player.currentPosition > 200){
                            visibleState.value = false
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)

                        // everytime media item changes notify playlist about current playing
                        onVideoChange(
                            this@apply.currentPeriodIndex
                        )

                        // everytime the media is started playing, show the title
                        visibleState.value = true
                        videoTitle.value = mediaItem?.mediaMetadata?.displayTitle.toString()
                    }
                }
            )
        }
    }


    ConstraintLayout(modifier = modifier) {
        val (title, videoPlayer) = createRefs()

        // Video title

        AnimatedVisibility(
            visible = visibleState.value,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {

            Text(
                text = "Video Title",
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

        }

        // Player View

        DisposableEffect(
            AndroidView(
                modifier = Modifier
                    .testTag("VideoPlayer")
                    .constrainAs(videoPlayer) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                factory = {
                    // ExoplayerView for video player
                    PlayerView(context).apply {
                        player = exoPlayer
                        layoutParams =
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                    }
                })
        ) {
            onDispose {

                // release exoplayer when no longer needed

                exoPlayer.release()
            }
        }
    }
}


@Composable
fun VideoPlayList(
    modifier : Modifier = Modifier,
    videos : List<VideoResultEntity>,
    currentPlaying: Int,
    onVideoChange: (Int) -> Unit
){
    LazyColumn(modifier = Modifier){
        itemsIndexed(
            items = videos,
            key = { _, item ->
                item.id
            }
        ){ index, item ->
            VideoItem(index = index,video = item,
                currentPlaying = currentPlaying){ index ->
                onVideoChange(index)
            }

        }
    }
}


@Composable
fun VideoItem(index : Int, video : VideoResultEntity,currentPlaying: Int, onVideoChange: (Int) -> Unit){
    val currentlyPlaying = remember {
        mutableStateOf(false)
    }

    currentlyPlaying.value = currentPlaying == index

    ConstraintLayout(
        modifier = Modifier
            .testTag("VideoParent")
            .padding(8.dp)
            .wrapContentSize()
            .clickable {
                onVideoChange(index)
            }
    ) {
        val (thumbnail, play, title, nowPlaying) = createRefs()

        //Thumbnail

        androidx.compose.foundation.Image(
            contentScale = ContentScale.Crop,
            painter = rememberImagePainter(
                data = video.preview,
                builder = {
                    placeholder(R.drawable.ic_launcher_background)
                    crossfade(true)
                }
            ),
            contentDescription = "Thumbnail" ,
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .shadow(elevation = 20.dp)
                .constrainAs(thumbnail) {
                    top.linkTo(
                        parent.top,
                        margin = 8.dp,
                    )

                    start.linkTo(
                        parent.start,
                        margin = 8.dp
                    )

                    bottom.linkTo(parent.bottom)
                }
        )

        //Title

        Text(text = video.name,
            modifier = Modifier.constrainAs(title){
                top.linkTo(thumbnail.top, margin = 8.dp)
                start.linkTo(thumbnail.end,
                    margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width = Dimension.preferredWrapContent
                height = Dimension.wrapContent
            },
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            softWrap = true
            )

        // Divider

        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .testTag("Divider"),
            color = Color(0xFFE0E0E0)
        )


        if(currentlyPlaying.value){
            // Play Button Image
           Image(
               contentScale = ContentScale.Crop,
               colorFilter = if(video.preview.isEmpty()){
                                                        ColorFilter.tint(Color.White)
                                                        }else{
                                                             ColorFilter.tint(Color(0xFFF50057))
                                                             },
               contentDescription ="Playing",
               painter = painterResource(id = com.google.android.exoplayer2.R.drawable.exo_icon_play),
               modifier =
               Modifier
                   .height(50.dp)
                   .width(50.dp)
                   .graphicsLayer {
                       clip = true
                       shadowElevation = 20.dp.toPx()
                   }
                   .constrainAs(play) {
                       top.linkTo(thumbnail.top)
                       start.linkTo(thumbnail.start)
                       end.linkTo(thumbnail.end)
                       bottom.linkTo(thumbnail.bottom)
                   }
           )

            // Now playing text
            Text(
                text = "Now Playing",
                color = Color(0xFFF50057),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier =
                Modifier.constrainAs(nowPlaying) {
                    top.linkTo(
                        title.bottom,
                        margin = 8.dp
                    )
                    start.linkTo(
                        thumbnail.end,
                        margin = 8.dp
                    )
                    bottom.linkTo(
                        thumbnail.bottom,
                        margin = 8.dp
                    )
                    end.linkTo(
                        parent.end,
                        margin = 8.dp
                    )
                    width =
                        Dimension.preferredWrapContent
                    height =
                        Dimension.preferredWrapContent
                }
            )
        }
    }
}
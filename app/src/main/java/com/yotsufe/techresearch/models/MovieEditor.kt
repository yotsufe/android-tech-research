package com.yotsufe.techresearch.models

import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MovieEditor {

    companion object {
        fun append(dirPath: String, fileName1: String, fileName2: String) {
            val movie1 = MovieCreator.build("${dirPath}/$fileName1")
            val movie2 = MovieCreator.build("${dirPath}/$fileName2")
            val inMovies = arrayOf<Movie>(movie1, movie2)

            val videoTracks = LinkedList<Track>()
            val audioTracks = LinkedList<Track>()
            for (m in inMovies) {
                for (t in m.tracks) {
                    if (t.handler == "soun") {
                        audioTracks.add(t)
                    }
                    if (t.handler == "vide") {
                        videoTracks.add(t)
                    }
                }
            }

            val result = Movie()
            if (audioTracks.size > 0) {
                result.addTrack(AppendTrack(audioTracks[0], audioTracks[1]))
            }
            if (videoTracks.size > 0) {
                result.addTrack(AppendTrack(videoTracks[0], videoTracks[1]))
            }

            val out = DefaultMp4Builder().build(result)
            val outputFilePath = "$dirPath/rec_pager_test_full_temp.mp4"
            val fos = FileOutputStream(File(outputFilePath))
            out.writeContainer(fos.channel)
            fos.close()

            val isDeletedFile1 = File("${dirPath}/$fileName1").delete()
            val isDeletedFile2 = File("${dirPath}/$fileName2").delete()
            File("$dirPath/rec_pager_test_full_temp.mp4").renameTo(File("$dirPath/rec_pager_test_full.mp4"))
        }

    }

}

package net.pettip.app.data.utils

import net.pettip.app.domain.entity.map.GPX_CREATOR
import net.pettip.app.domain.entity.map.GPX_DATE_FORMAT
import net.pettip.app.domain.entity.map.GPX_DECIMAL_FORMAT_1
import net.pettip.app.domain.entity.map.GPX_DECIMAL_FORMAT_3
import net.pettip.app.domain.entity.map.GPX_DECIMAL_FORMAT_7
import net.pettip.app.domain.entity.map.GPX_NAMESPACE
import net.pettip.app.domain.entity.map.GPX_TICK_FORMAT
import net.pettip.app.domain.entity.map.GPX_VERSION
import net.pettip.app.domain.entity.map.GPX_XSI_NAMESPACE
import net.pettip.app.domain.entity.map.Track
import net.pettip.app.domain.entity.map._GPX
import java.io.File
import java.util.Date

/**
 * @Project     : PetTip-Android
 * @FileName    : GPXWriter
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.data.utils
 * @see net.pettip.app.data.utils.GPXWriter
 */
class GPXWriter: _GPX() {

 fun write(tracks: List<Track>, file: File) {
  if (tracks.isEmpty()) {
   return
  }
  val firstTime = GPX_TICK_FORMAT.format(Date(tracks.first().time))

  val comment = """
                <!-- Created with PetTip -->
                <!-- Track = ${tracks.size} TrackPoints + 0 Placemarks -->
                <!-- Track Statistics (based on Total Time | Time in Movement): -->
                <!-- Distance = ${calculateTotalDistance(tracks)} -->
                <!-- Duration = ${calculateDuration(tracks)} | N/A -->
                <!-- Altitude Gap = ${calculateMaxAltitudeGap(tracks)} -->
                <!-- Max Speed = ${calculateMaxSpeed(tracks)} m/s -->
                <!-- Avg Speed = ${calculateAvgSpeed(tracks)} | N/A -->
                <!-- Direction = N/A -->
                <!-- Activity = N/A -->
                <!-- Altitudes = N/A -->
            """.trimIndent() + "\n"

  val metadata = """
                <metadata>
                 <name>PetTip $firstTime</name>
                 <time>${GPX_DATE_FORMAT.format(tracks.first().time)}</time>
                </metadata>
            """.trimIndent() + "\n"

  val header = """
                <gpx version="$GPX_VERSION"
                     creator="$GPX_CREATOR"
                     xmlns="$GPX_NAMESPACE"
                     xmlns:xsi="$GPX_XSI_NAMESPACE"
                     xsi:schemaLocation="$GPX_NAMESPACE http://www.topografix.com/GPX/1/1/gpx.xsd">
            """.trimIndent() + "\n"

  val footer = "</gpx>"

  val trksegStringBuilder = StringBuilder()

  for (track in tracks) {
   val lat = GPX_DECIMAL_FORMAT_7.format(track.latitude)
   val lon = GPX_DECIMAL_FORMAT_7.format(track.longitude)
   val time = GPX_DATE_FORMAT.format(track.time)
   val speed = GPX_DECIMAL_FORMAT_3.format(track.speed)
   val ele = GPX_DECIMAL_FORMAT_3.format(track.altitude)
   val bearing = GPX_DECIMAL_FORMAT_1.format(track.bearing)
   val no = track.no
   val event = track.event
   val uri = track.uri
   val trkpt = """ <trkpt no="${no}" event="${event}" lat="${lat}" lon="${lon}"><time>$time</time><speed>$speed</speed><bearing>$bearing</bearing><ele>$ele</ele><uri>$uri</uri></trkpt>""" + "\n"
   trksegStringBuilder.append(trkpt)
  }

  val trkseg = "<trkseg>\n$trksegStringBuilder</trkseg>\n"
  val trk = "<trk>\n<name>$firstTime</name>\n$trkseg</trk>\n"
  val content = "$header$metadata$trk$footer"

  file.writeText(comment + content)
 }
}

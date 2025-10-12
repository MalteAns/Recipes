package de.malteans.recipes.core.presentation.details.components

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CustomDownloadDoneIcon: ImageVector
    get() {
        if (_CustomDownloadDoneIcon != null) {
            return _CustomDownloadDoneIcon!!
        }
        _CustomDownloadDoneIcon = ImageVector.Builder(
            name = "Download_done",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(382f, 640f)
                lineTo(155f, 413f)
                lineToRelative(57f, -57f)
                lineToRelative(170f, 170f)
                lineToRelative(366f, -366f)
                lineToRelative(57f, 57f)
                close()
                moveTo(200f, 800f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(80f)
                close()
            }
        }.build()
        return _CustomDownloadDoneIcon!!
    }

private var _CustomDownloadDoneIcon: ImageVector? = null

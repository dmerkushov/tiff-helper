tiff-helper
===========

English
-------

This library was developed mostly to use with ePhoenix, a European Patent Office's standard tool to work with electronic documents concerning patent applications.

The primary class to use is `ru.dmerkushov.TiffImage`. It includes the following constructors:

* `TiffImage ()` - creates an empty image with width and height both 0
* `TiffImage (int width, int height)` - creates an empty (white) image
* `TiffImage (File tiffImageFile)` - creates an image from a given TIFF file
* `TiffImage (String tiffImageFileName)` - creates an image from a given TIFF file
* `TiffImage (RenderedImage renderedImage, long resolutionDpi)` - creates an image from `java.awt.image.RenderedImage` instance, having specified TIFF resolition in ppi
* `TiffImage (RenderedImage renderedImage, long xResolution, long yResolution, char resolutionUnit)` - creates an image from `java.awt.image.RenderedImage` instance, having specified horizontal and vertical TIFF resolitions in given pixels per unit (units are `TiffImage.RESOLUTION_UNIT_INCH` for inch or `TiffImage.RESOLUTION_UNIT_CENTIMETER` for centimeter)

A pixel is an array of int. Three integers are included: one for the red component, one for the green component, and one for the blue component. Their indexes in the array are `TiffImage.RED`, `TiffImage.GREEN`, and `TiffImage.BLUE`.

The image can be stamped by another `TiffImage` using the `stamp ()` method. It takes the stamp `TiffImage` instance and its desired position as parameters.

The image can be saved as TIFF using the following methods:

* `saveTiffBWG4 (File file)` - save as CCITT G4 one-strip TIFF
* `saveTiffGrayscalePackbits (File file)` - save as grayscale TIFF with PACKBITS compression
* `saveTiffColorPackbits (File file)` - save as color TIFF with PACKBITS compression

Other methods are quite usual: these are getters and setters for pixels, image size, and so on.

### tiffrenderer

**tiff-helper** makes use of the tiffrenderer library, which was written for Apache FOP and is an implementation of JAI-like classes, but available in Maven Central.

JAI stands for Java Advanced Imaging API by Sun Microsystems.

Russian
-------

Данная библиотека была разработана, в основном, для использования с ePhoenix, инструментом, написанным в Европейском патентном ведомстве для работы с документами, связанными с заявками на патенты и патентами.

Основной класс, который предполагается использовать, это `ru.dmerkushov.TiffImage`. Конструкторы:

* `TiffImage ()` - создаёт пустое изображение, шириной и высотой 0
* `TiffImage (int width, int height)` - создаёт пустое (белое) изображение данной ширины и высоты
* `TiffImage (File tiffImageFile)` - создаёт изображение из данного файла TIFF
* `TiffImage (String tiffImageFileName)` - cсоздаёт изображение из данного файла TIFF
* `TiffImage (RenderedImage renderedImage, long resolutionDpi)` - создаёт изображение из экземпляра `java.awt.image.RenderedImage`, для которого указано разрешение
* `TiffImage (RenderedImage renderedImage, long xResolution, long yResolution, char resolutionUnit)` - создаёт изображение из экземпляра `java.awt.image.RenderedImage`, для которого указано горизонтальное и вертикальное разрешение, а также единицы измерения длины в разрешении (`TiffImage.RESOLUTION_UNIT_INCH` - дюйм или `TiffImage.RESOLUTION_UNIT_CENTIMETER` - сантиметр)

Пиксел - массив типа int. Включает три целых числа: одно для красного компонента цвета, одно для зелёного, одно для синего. Их индексы в массиве - соответственно `TiffImage.RED`, `TiffImage.GREEN` и `TiffImage.BLUE`.

На изображение может быть наложен штамп - другой экземпляр `TiffImage` - с помощью метода `stamp ()`. Метод принимает в качестве параметров - изображение штампа как `TiffImage` и его размещение на данном изображении.

Изображение может быть сохранено как файл TIFF с помощью следующих методов:

* `saveTiffBWG4 (File file)` - CCITT G4 one-strip TIFF
* `saveTiffGrayscalePackbits (File file)` - TIFF в оттенках серого (grayscale), сжатие PACKBITS
* `saveTiffColorPackbits (File file)` - цветной TIFF, сжатие PACKBITS

### tiffrenderer

**tiff-helper** использует библиотеку tiffrenderer, которая была написана для Apache FOP и реализует JAI-подобные классы, в отличие от JAI, доступные в репозитории Maven Central.

JAI - Java Advanced Imaging API by Sun Microsystems.

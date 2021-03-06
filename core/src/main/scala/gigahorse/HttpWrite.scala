/*
 * Copyright 2016 by Eugene Yokota
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gigahorse

import java.nio.charset.Charset

abstract class HttpWrite[A] {
  def toByteArray(a: A): Array[Byte]
  def contentType: Option[String]
}

object HttpWrite {
  val utf8 = Charset.forName("UTF-8")
  implicit val stringHttpWrite: HttpWrite[String] = new StringHttpWrite
  private final class StringHttpWrite extends HttpWrite[String] {
    def toByteArray(a: String): Array[Byte] = a.getBytes(utf8)
    def contentType: Option[String] = None
  }
  implicit val encodedStringHttpWrite: HttpWrite[EncodedString] = new EncodedStringHttpWrite
  private final class EncodedStringHttpWrite extends HttpWrite[EncodedString] {
    def toByteArray(a: EncodedString): Array[Byte] = a.string.getBytes(a.charset)
    def contentType: Option[String] = None
  }
  implicit val urlEncodedFormHttpWrite: HttpWrite[Map[String, List[String]]] = new UrlEncodedFormHttpWrite
  private final class UrlEncodedFormHttpWrite extends HttpWrite[Map[String, List[String]]] {
    import java.net.URLEncoder
    def toByteArray(formData: Map[String, List[String]]): Array[Byte] =
      (formData map { case (k, vs) =>
        vs.map(c => k + "=" + URLEncoder.encode(c, "UTF-8"))
      }).flatten.mkString("&").getBytes(utf8)
    def contentType: Option[String] = Some(ContentTypes.FORM)
  }
}

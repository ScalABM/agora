/*
Copyright 2016 ScalABM

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import java.util.UUID

package object markets {

  /** Base trait for all messages. */
  trait Message {

    def timestamp: Long

    def uuid: UUID

  }

  /** Base trait for representing contracts. */
  trait Contract extends Message {

    /** The actor for whom the `Contract` is a liability. */
    def issuer: UUID

    /** The actor for whom the `Contract` is an asset. */
    def counterparty: Option[UUID]

  }

}

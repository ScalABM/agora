/*
Copyright 2015 David R. Pugh, J. Doyne Farmer, and Dan F. Tang

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
package markets.tickers

import akka.agent.Agent

import scala.concurrent.{ExecutionContext, Future}


class Ticker[T](initialValue: T) extends Agent[T] {

  def alter(newValue: T): Future[T] = {
    super.alter(newValue)
  }

  def alter(f: T => T): Future[T] = {
    super.alter(f)
  }

  def alterOff(f: T => T)(implicit ec: ExecutionContext): Future[T] = {
    super.alterOff(f)(ec)
  }

  def flatMap[B](f: T => Agent[B]): Agent[B] = {
    super.flatMap(f)
  }

  def foreach[U](f: T => U): Unit = {
    super.foreach(f)
  }

  def future(): Future[T] = {
    super.future()
  }

  def get(): T = {
    super.get()
  }

  def map[B](f: T => B): Agent[B] = {
    super.map(f)
  }

  def send(newValue: T): Unit = {
    super.send(newValue)
  }

  def send(f: T => T): Unit = {
    super.send(f)
  }

  def sendOff(f: T => T)(implicit ec: ExecutionContext): Unit = {
    super.sendOff(f)(ec)
  }

}


object Ticker {

  def apply[T](initialValue: T): Ticker[T] = {
    new Ticker(initialValue)
  }

}
/**
  * Copyright 2015-2016 INESC-ID
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
**/

package domainregistry;

class MetricsThread extends Thread{
    private static final int TWO_SECONDS = 2000;
    HypertyController controller;

    public MetricsThread(HypertyController controller){
        this.controller = controller;
    }

    @Override
    public void run(){
        try{
            while(true){
                Thread.sleep(TWO_SECONDS);
                double writes = (double) controller.getNumWrites();
                double reads =  (double) controller.getNumReads();
                RiemannCommunicator.send("http get", "http", reads);
                RiemannCommunicator.send("http put", "http", writes);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}

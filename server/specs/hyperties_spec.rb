# Copyright 2015-2016 inesc-id-ID
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'airborne'

describe 'domain registry api tests' do

  before {
    @hyperty_zero_details = {
      resources: ["chat", "voice"],
      dataSchemes: ["comm"],
      descriptor: "descriptor1",
      expires: 0,
      status: "disconnected",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid44"
    }

    @hyperty_details = {
      resources: ["chat", "voice"],
      dataSchemes: ["comm"],
      descriptor: "descriptor1",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid4"
    }

    @hyperty_two_details = {
      resources: ["chat", "voice", "video"],
      dataSchemes: ["comm"],
      descriptor: "descriptor2",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid222"
    }

    @hyperty_two_v2_details = {
      resources: ["chatt"],
      dataSchemes: ["comm"],
      descriptor: "descriptor2",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid222"
    }

    @hyperty_three_details = {
      resources: ["chat", "voice", "video"],
      dataSchemes: ["comm", "fake"],
      descriptor: "descriptor3",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid4"
    }

    @hyperty_four_details = {
      resources: ["chat", "video"],
      dataSchemes: ["comm"],
      descriptor: "descriptor4",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid4"
    }

    @hyperty_ten_details = {
      resources: ["chat", "video"],
      dataSchemes: ["comm"],
      descriptor: "descriptor4",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid_10"
    }

    @hyperty_five_details = {
      resources: ["chat", "voice"],
      dataSchemes: ["comm"],
      descriptor: "descriptor5",
      expires: 1200,
      status: "created",
      runtime: "runtime",
      p2pRequester: "requester",
      p2pHandler: "handler",
      guid: "guid_5"
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do #non-existent user creates non-existent hyperty
      put host << '/hyperty/user/ruijose@inesc-id.pt/hyperty1', @hyperty_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do
      put host << '/hyperty/user/user%3A%2F%2Fgoogle.com%2Fbernardo.marquesg@gmail.com/hyperty7', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do
      put host << '/hyperty/user/user%3A%2F%2Fgoogle.com%2Fbernardo.marquesg@gmail.com/hyperty81', @hyperty_two_v2_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty with expires being zero' do
      put host << '/hyperty/user/user%3A%2F%2Fgoogle.com%2Frui.marquesg@gmail.com/hyperty77', @hyperty_zero_details
      expect_status(200)
      expect_json_types('runtimes': :array)
    end

    it 'should add a new hyperty' do
      put host << '/hyperty/user/ruigil@inesc-id.pt/hyperty11', @hyperty_five_details
      expect_status(200)
      expect_json_types('runtimes': :array)
    end

    it 'should add a new hyperty' do #hyperty already exists and belongs to user: ruijose
      put host << '/hyperty/user/ruimangas@inesc-id.pt/hyperty1', @hyperty_details
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should add a new hyperty' do #existent user creates non-existent hyperty
      put host << '/hyperty/user/ruijose@inesc-id.pt/hyperty2', @hyperty_four_details
      expect_status(200)
      expect_json_types('runtimes': :array)
    end

    it 'should add a new hyperty' do #existent user creates non-existent hyperty
      put host << '/hyperty/user/ruigarcia@inesc-id.pt/hyperty3', @hyperty_ten_details
      expect_status(200)
      expect_json_types('runtimes': :array)
    end

    it 'should add a new hyperty' do #existent user tries to create or update another user's hyperty
      put host << '/hyperty/user/ruijose@inesc-id.pt/hyperty3', @hyperty_four_details
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should add a new hyperty' do ##existent user creates non-existent hyperty
      put host << '/hyperty/user/ruijose@inesc-id.pt/hyperty6', @hyperty_four_details
      expect_status(200)
      expect_json_types('runtimes': :array)
    end
  end

  describe 'get specific hyperty' do
    it 'should return an hyperty' do
      get host << '/hyperty/url/hyperty3'
      expect_status(200)
      expect_json_sizes(13)
      expect_json_keys([:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid, :userID, :hypertyID])
    end

    it 'should return an hyperty' do
      get host << '/hyperty/url/hyperty61'
      expect_status(404)
      expect_json_sizes(1)
      expect_json_keys([:message])
    end
  end

  describe 'get hyperties by guid' do
    it 'should return four hyperties' do
      get host << '/hyperty/guid/guid4'
      expect_status(200)
      expect_json_sizes(3)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_keys("hyperty6", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_types("hyperty1", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect_json_types("hyperty2", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect_json_types("hyperty6", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect(json_body[:hyperty1][:descriptor]).to eql("descriptor1")
      expect(json_body[:hyperty2][:descriptor]).to eql("descriptor4")
      expect(json_body[:hyperty6][:descriptor]).to eql("descriptor4")
      expect(json_body[:hyperty1][:expires]).to eql(1200)
      expect(json_body[:hyperty2][:expires]).to eql(1200)
      expect(json_body[:hyperty6][:expires]).to eql(1200)
      expect(json_body[:hyperty1][:resources]).to eql(["chat", "voice"])
      expect(json_body[:hyperty2][:resources]).to eql(["chat", "video"])
      expect(json_body[:hyperty6][:resources]).to eql(["chat", "video"])
      expect(json_body[:hyperty1][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty2][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty6][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty1][:status]).to eql("created")
      expect(json_body[:hyperty2][:status]).to eql("created")
      expect(json_body[:hyperty6][:status]).to eql("created")
      expect(json_body[:hyperty1][:guid]).to eql("guid4")
      expect(json_body[:hyperty2][:guid]).to eql("guid4")
      expect(json_body[:hyperty6][:guid]).to eql("guid4")
    end

    it 'should return one hyperty' do
      get host << '/hyperty/guid/guid_10'
      expect_status(200)
      expect_json_sizes(1)
      expect_json_keys('hyperty3', [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
    end

    it 'should return hyperties not found error' do
      get host << '/hyperty/guid/notexist_guid'
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end

  describe 'get all hyperties' do
    it 'should return all the hyperties' do
      get host << '/hyperty/user/ruijose@inesc-id.pt'
      expect_status(200)
      expect_json_sizes(3)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_types("hyperty1", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect_json_types("hyperty2", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect(json_body[:hyperty1][:descriptor]).to eql("descriptor1")
      expect(json_body[:hyperty2][:descriptor]).to eql("descriptor4")
      expect(json_body[:hyperty6][:descriptor]).to eql("descriptor4")
      expect(json_body[:hyperty1][:expires]).to eql(1200)
      expect(json_body[:hyperty2][:expires]).to eql(1200)
      expect(json_body[:hyperty6][:expires]).to eql(1200)
      expect(json_body[:hyperty1][:resources]).to eql(["chat", "voice"])
      expect(json_body[:hyperty2][:resources]).to eql(["chat", "video"])
      expect(json_body[:hyperty6][:resources]).to eql(["chat", "video"])
      expect(json_body[:hyperty1][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty2][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty6][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty1][:status]).to eql("created")
      expect(json_body[:hyperty2][:status]).to eql("created")
      expect(json_body[:hyperty6][:status]).to eql("created")
      expect(json_body[:hyperty1][:guid]).to eql("guid4")
      expect(json_body[:hyperty2][:guid]).to eql("guid4")
      expect(json_body[:hyperty6][:guid]).to eql("guid4")
    end

    it 'should return an error, user not found' do
      get host << '/hyperty/user/nuno@inesc-id.pt'
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end

  describe 'get hyperties by email' do
    it 'should return one hyperty' do
      get host << '/hyperty/email/bernardo.marquesg@gmail.com'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty7", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler])
      expect(json_body[:hyperty7][:descriptor]).to eql("descriptor2")
      expect(json_body[:hyperty7][:expires]).to eql(1200)
      expect(json_body[:hyperty7][:resources]).to eql(["chat", "voice", "video"])
      expect(json_body[:hyperty7][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty7][:status]).to eql("created")
      expect_json_keys("hyperty81", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler])
      expect(json_body[:hyperty81][:descriptor]).to eql("descriptor2")
      expect(json_body[:hyperty81][:expires]).to eql(1200)
      expect(json_body[:hyperty81][:resources]).to eql(["chatt"])
      expect(json_body[:hyperty81][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty81][:status]).to eql("created")
    end

    it 'should return an error, email not found' do
      get host << '/hyperty/email/testfake@inesc-id.pt'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return an error, hyperty has already expires' do
      get host << '/hyperty/email/rui.marquesg@gmail.com'
      expect_status(408)
      expect_json_sizes(1)
    end
  end

  describe 'get advanced search by email' do
    it 'should return one hyperty' do
      get host << '/hyperty/email/bernardo.marquesg@gmail.com/hyperty?resources=chat'
      expect_status(200)
      expect_json_sizes(1)
      expect_json_keys("hyperty7", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler])
      expect(json_body[:hyperty7][:descriptor]).to eql("descriptor2")
      expect(json_body[:hyperty7][:expires]).to eql(1200)
      expect(json_body[:hyperty7][:resources]).to eql(["chat", "voice", "video"])
      expect(json_body[:hyperty7][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty7][:status]).to eql("created")
    end

    it 'should return one hyperty' do
      get host << '/hyperty/email/bernardo.marquesg@gmail.com/hyperty?resources=chat&dataSchemes=comm'
      expect_status(200)
      expect_json_sizes(1)
      expect_json_keys("hyperty7", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler])
      expect(json_body[:hyperty7][:descriptor]).to eql("descriptor2")
      expect(json_body[:hyperty7][:expires]).to eql(1200)
      expect(json_body[:hyperty7][:resources]).to eql(["chat", "voice", "video"])
      expect(json_body[:hyperty7][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty7][:status]).to eql("created")
    end

    it 'should return two hyperties' do
      get host << '/hyperty/email/bernardo.marquesg@gmail.com/hyperty?dataSchemes=comm'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty7", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler])
      expect(json_body[:hyperty7][:descriptor]).to eql("descriptor2")
      expect(json_body[:hyperty7][:expires]).to eql(1200)
      expect(json_body[:hyperty7][:resources]).to eql(["chat", "voice", "video"])
      expect(json_body[:hyperty7][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty7][:status]).to eql("created")
      expect_json_keys("hyperty81", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler])
      expect(json_body[:hyperty81][:descriptor]).to eql("descriptor2")
      expect(json_body[:hyperty81][:expires]).to eql(1200)
      expect(json_body[:hyperty81][:resources]).to eql(["chatt"])
      expect(json_body[:hyperty81][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty81][:status]).to eql("created")
    end

    it 'should return one hyperty' do
      get host << '/hyperty/email/bernardo.marquesg@gmail.com/hyperty?dataSchemes=bernardo'
      expect_status(404)
    end

    describe 'live and disconnected hyperties' do
      before(:all) do
        @disconnected_1 = {
          resources: ["chat", "voice"],
          dataSchemes: ["comm"],
          descriptor: "descriptor1",
          expires: 1200,
          status: "disconnected",
          runtime: "runtime",
          p2pRequester: "requester",
          p2pHandler: "handler",
          guid: "guid44"
        }

        @disconnected_2 = {
          resources: ["chat", "voice"],
          dataSchemes: ["comm"],
          descriptor: "descriptor12",
          expires: 1200,
          status: "disconnected",
          runtime: "runtime",
          p2pRequester: "requester",
          p2pHandler: "handler",
          guid: "guid44"
        }

        @live_1 = {
          resources: ["chat", "voice"],
          dataSchemes: ["comm"],
          descriptor: "descriptor12",
          expires: 1200,
          status: "live",
          runtime: "runtime",
          p2pRequester: "requester",
          p2pHandler: "handler",
          guid: "guid44"
        }

        put host << '/hyperty/user/user%3A%2F%2Fgoogle.com%2Frui.jose@gmail.com/disconnected1', @disconnected_1
        put host << '/hyperty/user/user%3A%2F%2Fgoogle.com%2Frui.jose@gmail.com/disconnected2', @disconnected_2
      end

      it 'should return two disconnected hyperties' do
        get host << '/hyperty/email/rui.jose@gmail.com'
        expect_status(408);
        expect_json_sizes(2)
      end

      it 'should return one live hyperty, the other two are disconnected' do
        put host << '/hyperty/user/user%3A%2F%2Fgoogle.com%2Frui.jose@gmail.com/live_1', @live_1
        get host << '/hyperty/email/rui.jose@gmail.com'
        expect_status(200);
        expect_json_sizes(1)
      end

      it 'should return two live hyperty, after on of the disconnected comes back alive' do
        put host << '/hyperty/url/disconnected1', {}
        get host << '/hyperty/email/rui.jose@gmail.com'
        expect_status(200);
        expect_json_sizes(2)
      end

      it 'should return three live hyperty, after the remaining disconnected comes back alive' do
        put host << '/hyperty/url/disconnected2', {}
        get host << '/hyperty/email/rui.jose@gmail.com'
        expect_status(200);
        expect_json_sizes(3)
      end
    end
  end

  describe 'get specific hyperties' do

    it 'should return all user hyperties with voice resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?resources=voice'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return all user hyperties with voice resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?dataSchemes=comm'
      expect_status(200);
      expect_json_sizes(3)
    end

    it 'should return all user hyperties with voice resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?dataSchemes=comm&resources=chat,video'
      expect_status(200);
      expect_json_sizes(2)
    end

    it 'should return all user hyperties with voice resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?dataSchemes=comm&resources=voice'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return all user hyperties with voice and chat as resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?resources=voice,chat'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return all user hyperties with video resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?resources=video'
      expect_status(200);
      expect_json_sizes(2)
    end

    it 'should return all user hyperties with chat resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?resources=chat'
      expect_status(200);
      expect_json_sizes(3)
    end

    it 'should return a hyperties not found error' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?resources=messaging'
      expect_status(404);
      expect_json(:message => "Not Found")
    end

    it 'should return a hyperties not found error' do
      get host << '/hyperty/user/ruigil@inesc-id.pt/hyperty?dataSchemes=comm&resources=video'
      expect_status(404);
      expect_json(:message => "Not Found")
    end

    it 'should return a missing query string error' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/messaging'
      expect_status(404);
      expect_json(:message => "Not Found")
    end
  end

  describe 'update and get all hyperties' do

    it 'should update a hyperty' do
      sleep(1)
      put host << '/hyperty/user/ruijose@inesc-id.pt/hyperty1', @hyperty_three_details
      expect_status(200)
      expect_json_types('runtimes': :array)
    end

    it 'should get all updated hyperties' do
      get host << '/hyperty/user/ruijose@inesc-id.pt'
      expect_status(200)
      expect_json_sizes(3)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      expect_json_types("hyperty1", resources: :array_of_strings, dataSchemes: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect_json_types("hyperty2", resources: :array_of_strings, dataSchemes: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
      expect(json_body[:hyperty1][:resources]).to eql(["chat", "voice", "video"])
      expect(json_body[:hyperty1][:dataSchemes]).to eql(["comm", "fake"])
      expect(json_body[:hyperty1][:expires]).to eql(1200)
      expect(json_body[:hyperty1][:descriptor]).to eql("descriptor3")
      expect(json_body[:hyperty2][:expires]).to eql(1200)
      expect(json_body[:hyperty1][:guid]).to eql("guid4")
      expect(json_body[:hyperty2][:guid]).to eql("guid4")
    end

    it 'should return all user hyperties with voice resource type' do
      get host << '/hyperty/user/ruijose@inesc-id.pt/hyperty?dataSchemes=comm,fake&resources=voice,chat,voice'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return an error, user not found' do
      get host << '/hyperty/user/nuno@inesc-id.pt'
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end

  describe 'delete user hyperty' do
    it 'should return an error, user not found' do
      delete host << '/hyperty/user/ruijose12@inesc-id.pt/hyperty1'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return an error, user was found, but the hyperty did not exist' do
      delete host << '/hyperty/user/ruijose@inesc-id.pt/hyperty111'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should delete an user hyperty' do
      delete host << '/hyperty/user/ruijose@inesc-id.pt/hyperty1'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete host << '/hyperty/user/ruijose@inesc-id.pt/hyperty2'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should return an error, user was found, but hyperty belongs to another one' do
      delete host << '/hyperty/user/ruijose@inesc-id.pt/hyperty3'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should delete the hyperty' do
      delete host << '/hyperty/user/ruijose@inesc-id.pt/hyperty6'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should return an error, all this users hyperties were removed.' do
      delete host << '/hyperty/user/ruijose@inesc-id.pt/hyperty35'
      expect_status(404)
      expect_json(:message => "Not Found") #all this user's hyperties were removed
    end
  end
end

def host
  ENV['HOST'].dup
end

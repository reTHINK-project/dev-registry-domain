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

Airborne.configure do |config|
  config.base_url = ENV["HOST"].dup << '/hyperty/user'
end

describe 'domain registry api tests' do

  before {
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
      p2pHandler: "handler"
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
      put '/ruijose@inesc-id.pt/hyperty1', @hyperty_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do
      put '/ruigil@inesc-id.pt/hyperty11', @hyperty_five_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do #hyperty already exists and belongs to user: ruijose
      put '/ruimangas@inesc-id.pt/hyperty1', @hyperty_details
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should add a new hyperty' do #existent user creates non-existent hyperty
      put '/ruijose@inesc-id.pt/hyperty2', @hyperty_four_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do #existent user creates non-existent hyperty
      put '/ruigarcia@inesc-id.pt/hyperty3', @hyperty_ten_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do #existent user tries to create or update another user's hyperty
      put '/ruijose@inesc-id.pt/hyperty3', @hyperty_four_details
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should add a new hyperty' do ##existent user creates non-existent hyperty
      put '/ruijose@inesc-id.pt/hyperty6', @hyperty_four_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end
  end

  describe 'get specific hyperty' do
    Airborne.configure do |config|
      config.base_url = ENV["HOST"].dup << '/hyperty/url'
    end

    it 'should return an hyperty' do
      get '/hyperty3' do
        expect_status(200)
        expect_json_sizes(13)
        expect_json_keys([:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
      end
    end

    it 'should return an hyperty' do
      get '/hyperty61' do
        expect_status(404)
        expect_json_sizes(1)
        expect_json_keys([:message])
      end
    end

    describe 'get hyperties by guid' do
      Airborne.configure do |config|
        config.base_url = ENV["HOST"].dup << '/hyperty/guid'
      end
    end

      it 'should return four hiperties' do
        get '/guid4' do
          expect_status(200)
          expect_json_sizes(4)
          expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
          expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
          expect_json_keys("hyperty3", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
          expect_json_keys("hyperty6", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
          expect_json_types("hyperty1", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
          expect_json_types("hyperty2", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
          expect_json_types("hyperty3", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
          expect_json_types("hyperty6", dataSchemes: :array_of_strings, resources: :array_of_strings, descriptor: :string, startingTime: :string, lastModified: :string, expires: :int, status: :string, runtime: :string, p2pHandler: :string, p2pRequester: :string, guid: :string)
          expect(json_body[:hyperty1][:descriptor]).to eql("descriptor1")
          expect(json_body[:hyperty2][:descriptor]).to eql("descriptor4")
          expect(json_body[:hyperty3][:descriptor]).to eql("descriptor4")
          expect(json_body[:hyperty6][:descriptor]).to eql("descriptor4")
          expect(json_body[:hyperty1][:expires]).to eql(1200)
          expect(json_body[:hyperty2][:expires]).to eql(1200)
          expect(json_body[:hyperty3][:expires]).to eql(1200)
          expect(json_body[:hyperty6][:expires]).to eql(1200)
          expect(json_body[:hyperty1][:startingTime]).to eql(json_body[:hyperty1][:lastModified])
          expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
          expect(json_body[:hyperty3][:startingTime]).to eql(json_body[:hyperty3][:lastModified])
          expect(json_body[:hyperty6][:startingTime]).to eql(json_body[:hyperty6][:lastModified])
          expect(json_body[:hyperty1][:resources]).to eql(["chat", "voice"])
          expect(json_body[:hyperty2][:resources]).to eql(["chat", "voice"])
          expect(json_body[:hyperty3][:resources]).to eql(["chat", "voice"])
          expect(json_body[:hyperty6][:resources]).to eql(["chat", "voice"])
          expect(json_body[:hyperty1][:dataSchemes]).to eql(["comm"])
          expect(json_body[:hyperty2][:dataSchemes]).to eql(["comm"])
          expect(json_body[:hyperty3][:dataSchemes]).to eql(["comm"])
          expect(json_body[:hyperty6][:dataSchemes]).to eql(["comm"])
          expect(json_body[:hyperty1][:status]).to eql("created")
          expect(json_body[:hyperty2][:status]).to eql("created")
          expect(json_body[:hyperty3][:status]).to eql("created")
          expect(json_body[:hyperty6][:status]).to eql("created")
          expect(json_body[:hyperty1][:guid]).to eql("guid4")
          expect(json_body[:hyperty2][:guid]).to eql("guid4")
          expect(json_body[:hyperty3][:guid]).to eql("guid4")
          expect(json_body[:hyperty6][:guid]).to eql("guid4")
        end
      end

      it 'should return one hyperty' do
        get '/guid_10' do
          expect_status(404)
          expect_json_sizes(1)
          expect_json_keys("hypert3", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status, :runtime, :p2pRequester, :p2pHandler, :guid])
        end
      end

      it 'should return hyperties not found error' do
        get '/notexist_guid' do
          expect_status(404);
          expect_json(:message => "Not Found")
        end
      end

    Airborne.configure do |config|
      config.base_url = ENV["HOST"].dup << '/hyperty/user'
    end
  end

  describe 'get all hyperties' do

    it 'should return all the hyperties' do
      get '/ruijose@inesc-id.pt'
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
      expect(json_body[:hyperty1][:startingTime]).to eql(json_body[:hyperty1][:lastModified])
      expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
      expect(json_body[:hyperty6][:startingTime]).to eql(json_body[:hyperty6][:lastModified])
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
      get '/nuno@inesc-id.pt'
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end

  describe 'get specific hyperties' do

    it 'should return all user hyperties with voice resource type' do
      get '/ruijose@inesc-id.pt/hyperty?resources=voice'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return all user hyperties with voice resource type' do
      get '/ruijose@inesc-id.pt/hyperty?dataSchemes=comm'
      expect_status(200);
      expect_json_sizes(3)
    end

    it 'should return all user hyperties with voice resource type' do
      get '/ruijose@inesc-id.pt/hyperty?dataSchemes=comm&resources=chat,video'
      expect_status(200);
      expect_json_sizes(2)
    end

    it 'should return all user hyperties with voice resource type' do
      get '/ruijose@inesc-id.pt/hyperty?dataSchemes=comm&resources=voice'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return all user hyperties with voice and chat as resource type' do
      get '/ruijose@inesc-id.pt/hyperty?resources=voice,chat'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return all user hyperties with video resource type' do
      get '/ruijose@inesc-id.pt/hyperty?resources=video'
      expect_status(200);
      expect_json_sizes(2)
    end

    it 'should return all user hyperties with chat resource type' do
      get '/ruijose@inesc-id.pt/hyperty?resources=chat'
      expect_status(200);
      expect_json_sizes(3)
    end

    it 'should return a hyperties not found error' do
      get '/ruijose@inesc-id.pt/hyperty?resources=messaging'
      expect_status(404);
      expect_json(:message => "Not Found")
    end

    it 'should return a hyperties not found error' do
      get '/ruigil@inesc-id.pt/hyperty?dataSchemes=comm&resources=video'
      expect_status(404);
      expect_json(:message => "Not Found")
    end

    it 'should return a missing query string error' do
      get '/ruijose@inesc-id.pt/messaging'
      expect_status(404);
      expect_json(:message => "Not Found")
    end
  end

  describe 'update and get all hyperties' do

    it 'should update a hyperty' do
      sleep(1)
      put '/ruijose@inesc-id.pt/hyperty1', @hyperty_three_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should get all updated hyperties' do
      get '/ruijose@inesc-id.pt'
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
      expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
      expect(json_body[:hyperty1][:startingTime]).to be < (json_body[:hyperty1][:lastModified])
      expect(json_body[:hyperty1][:guid]).to eql("guid4")
      expect(json_body[:hyperty2][:guid]).to eql("guid4")
    end

    it 'should return all user hyperties with voice resource type' do
      get '/ruijose@inesc-id.pt/hyperty?dataSchemes=comm,fake&resources=voice,chat,voice'
      expect_status(200);
      expect_json_sizes(1)
    end

    it 'should return an error, user not found' do
      get '/nuno@inesc-id.pt'
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end

  describe 'delete user hyperty' do
    it 'should return an error, user not found' do
      delete '/ruijose12@inesc-id.pt/hyperty1'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return an error, user was found, but the hyperty did not exist' do
      delete '/ruijose@inesc-id.pt/hyperty111'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc-id.pt/hyperty1'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete '/ruijose@inesc-id.pt/hyperty2'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should return an error, user was found, but hyperty belongs to another one' do
      delete '/ruijose@inesc-id.pt/hyperty3'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should delete the hyperty' do
      delete '/ruijose@inesc-id.pt/hyperty6'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should return an error, all this users hyperties were removed.' do
      delete '/ruijose@inesc-id.pt/hyperty35'
      expect_status(404)
      expect_json(:message => "Not Found") #all this user's hyperties were removed
    end
  end
end

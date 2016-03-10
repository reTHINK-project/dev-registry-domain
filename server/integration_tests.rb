# Copyright 2015-2016 INESC-ID
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
  config.base_url = 'http://178.62.195.71:4567/hyperty/user'
end

describe 'domain registry api tests' do

  before {
    @hyperty_details = {
      descriptor: "kkk11jasdasdAA",
      expires: 120
    }

    @hyperty_two_details = {
      descriptor: "asdasd112AA",
      expires: 120
    }

    @hyperty_three_details = {
      descriptor: "12312321istuapt",
      expires: 120
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty1', @hyperty_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty2', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end
  end

  describe 'get all user hyperties' do

    it 'should return all the hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified, :expires])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified, :expires])
      expect_json_types("hyperty1", descriptor: :string, startingTime: :string, lastModified: :string, expires: :int)
      expect_json_types("hyperty2", descriptor: :string, startingTime: :string, lastModified: :string, expires: :int)
      expect(json_body[:hyperty1][:descriptor]).to eql("kkk11jasdasdAA")
      expect(json_body[:hyperty2][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:hyperty1][:expires]).to eql(120)
      expect(json_body[:hyperty2][:expires]).to eql(120)
      expect(json_body[:hyperty1][:startingTime]).to eql(json_body[:hyperty1][:lastModified])
      expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
    end

    it 'should return an error, user not found' do
      get '/nuno@inesc.pt'
      expect_status(404)
      expect_json(:message => "User not found")
    end
  end

  describe 'update and get all hyperties' do

    it 'should update a hyperty' do
      sleep(1)
      put '/ruijose@inesc.pt/hyperty1', @hyperty_three_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end

    it 'should get all updated hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified, :expires])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified, :expires])
      expect_json_types("hyperty1", descriptor: :string, startingTime: :string, lastModified: :string, expires: :int)
      expect_json_types("hyperty2", descriptor: :string, startingTime: :string, lastModified: :string, expires: :int)
      expect(json_body[:hyperty1][:descriptor]).to eql("12312321istuapt")
      expect(json_body[:hyperty2][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:hyperty1][:expires]).to eql(120)
      expect(json_body[:hyperty2][:expires]).to eql(120)
      expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
      expect(json_body[:hyperty1][:startingTime]).to be < (json_body[:hyperty1][:lastModified])
    end

    it 'should return an error, user not found' do
      get '/nuno@inesc.pt'
      expect_status(404)
      expect_json(:message => "User not found")
    end
  end

  describe 'delete user hyperty' do

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty1'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete '/ruijose@inesc.pt/hyperty2'
      expect_status(200)
      expect_json(:message => "Hyperty deleted")
    end

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty3'
      expect_status(404)
      expect_json(:message => "User not found") #all this user's hyperties were removed
    end

    it 'should return an error, user not found' do
      delete '/ruijose12@inesc.pt/hyperty1'
      expect_status(404)
      expect_json(:message => "User not found")
    end
  end
end

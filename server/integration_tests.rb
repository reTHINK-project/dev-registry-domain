require 'airborne'

Airborne.configure do |config|
  config.base_url = 'http://localhost:4567/hyperty/user'
end

describe 'domain registry api tests' do

  before {
    @hyperty_details = {
      descriptor: "kkk11jasdasdAA"
    }

    @hyperty_two_details = {
      descriptor: "asdasd112AA"
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty1', @hyperty_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty2', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end
  end

  describe 'get all user hyperties' do

    it 'should return all the hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified])
      expect_json_types("hyperty1", descriptor: :string, startingTime: :string, lastModified: :string)
      expect_json_types("hyperty2", descriptor: :string, startingTime: :string, lastModified: :string)
      expect_json("hyperty1.descriptor", "kk11jasdasdAA")
      expect_json("hyperty2.descriptor", "asdasd112AA")
    end

    it 'should return an error, user not found' do
      get '/nuno@inesc.pt'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'delete user hyperty' do

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty1'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete '/ruijose@inesc.pt/hyperty2'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty3'
      expect_status(400)
      expect_json(:message => "data not found")
    end

    it 'should return an error, user not found' do
      delete '/ruijose1@inesc.pt/hyperty1'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end
end

require 'airborne'

Airborne.configure do |config|
  config.base_url = 'http://localhost:4567/hyperty/user'
end

describe 'domain registry api tests' do

  before {
    @hyperty_details = {
      catalogAddress: "kkk11jasdasdAA",
      guid: "asdas999111",
      lastUpdate: "12-12-12"
    }

    @hyperty_two_details = {
      catalogAddress: "asdasd112AA",
      guid: "asdas999111ee",
      lastUpdate: "12-11-12"
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do
      put '/rui@skype.com/123AAdsadasdas', @hyperty_details
      expect_status(200)
      expect_json(:message => "hyperty created") 
    end

    it 'should add a new hyperty' do
      put '/pp@skype.com/X123AAdsadasdas', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end
  end

  describe 'get details about a single hyperty' do

    it 'should return the hyperty' do
      get '/rui@skype.com/123AAdsadasdas'
      expect_status(200)
      expect_json(@hyperty_details)
    end

    it 'should return the hyperty' do
      get '/pp@skype.com/X123AAdsadasdas'
      expect_status(200)
      expect_json(@hyperty_two_details)
    end

    it 'should return an error, data not found' do
      get '/rui@skype.com/0123AAdsadasdaspp'
      expect_status(400)
      expect_json(:message => "data not found")
    end

    it 'should return an error, user not found' do
      get '/joao@google.com/asdasdasd'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'get all user hyperties' do

    all_hyperties = {
      "123AAdsadasdas"   => @hyperty_details,
      "123AAdsadasdaspp" => @hyperty_two_details
    }

    it 'should return all the hyperties' do
      put '/rui@skype.com/123AAdsadasdaspp', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "hyperty created") 
      get '/rui@skype.com'
      expect_status(200)
      expect_json(all_hyperties)
    end

    it 'should return an error, user not found' do
      get '/joao@google.com'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'delete user hyperty' do

    it 'should delete an user hyperty' do
      delete '/rui@skype.com/123AAdsadasdaspp'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete '/pp@skype.com/X123AAdsadasdas'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should return an error, data not found' do
      delete '/rui@skype.com/0123AAdsadasdaspp'
      expect_status(400)
      expect_json(:message => "data not found")
    end

    it 'should return an error, user not found' do
      delete '/joao@google.com/asdsadas'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end
end

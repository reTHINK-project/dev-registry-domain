require 'airborne'

Airborne.configure do |config|
  config.base_url = 'http://localhost:4567'
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

  describe 'create user' do

    it 'should create a new user' do
      put '/user_id/rui@skype.com'
      expect_status(200)
      expect_json(:message => 'user created')
    end
  end

  describe 'create user hyperty' do

    it 'should add a new hyperty' do
      put '/user_id/rui@skype.com/123AAdsadasdas', @hyperty_details
      expect_status(200)
      expect_json(:message => "hyperty created") 
    end

    it 'should return an error, user not found' do
      put '/user_id/pp@skype.com/123AAdsadasdas'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'get details about a single hyperty' do

    it 'should return the hyperty' do
      get '/user_id//rui@skype.com/123AAdsadasdas'
      expect_status(200)
      expect_json(@hyperty_details)
    end

    it 'should return an error, user not found' do
      get '/user_id/pp@skype.com/123AAdsadasdaspp'
      expect_status(400)
      expect_json(:message => "user not found")
    end

    it 'should return an error, data not found' do
      get '/user_id/rui@skype.com/0123AAdsadasdaspp'
      expect_status(400)
      expect_json(:message => "data not found")
    end
  end

  describe 'get all user hyperties' do

    all_hyperties = {
      "123AAdsadasdas"   => @hyperty_details,
      "123AAdsadasdaspp" => @hyperty_two_details
    }

    it 'should return all the hyperties' do
      put '/user_id/rui@skype.com/123AAdsadasdaspp', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "hyperty created") 
      get '/user_id/rui@skype.com'
      expect_status(200)
      expect_json(all_hyperties)
    end

    it 'should return an error, user not found' do
      get '/user_id/pp@skype.com'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'delete user hyperty' do

    it 'should delete an user hyperty' do
      delete '/user_id/rui@skype.com/123AAdsadasdaspp'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should return an error, user not found' do
      delete '/user_id/pp@skype.com/123AAdsadasdaspp'
      expect_status(400)
      expect_json(:message => "user not found")
    end

    it 'should return an error, data not found' do
      delete '/user_id/rui@skype.com/0123AAdsadasdaspp'
      expect_status(400)
      expect_json(:message => "data not found")
    end
  end
end

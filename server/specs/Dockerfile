FROM ruby:2.2

RUN gem install rspec
RUN gem install airborne

WORKDIR /specs

ADD hyperties_spec.rb /specs/hyperties_spec.rb
ADD data_objects_spec.rb /specs/data_objects_spec.rb
ADD hyperty_keep_alive_spec.rb /specs/hyperty_keep_alive_spec.rb
ADD data_objects_keep_alive_spec.rb /specs/data_objects_keep_alive_spec.rb

CMD HOST=${SERVER}:${PORT} rspec hyperties_spec.rb && HOST=${SERVER}:${PORT} rspec data_objects_spec.rb && HOST=${SERVER}:${PORT} rspec hyperty_keep_alive_spec.rb && HOST=${SERVER}:${PORT} rspec data_objects_keep_alive_spec.rb

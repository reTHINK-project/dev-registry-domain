FROM ruby:2.2

RUN gem install rspec
RUN gem install airborne

WORKDIR /specs

ADD hyperties_spec.rb /specs/hyperties_spec.rb
ADD data_objects_spec.rb /specs/data_objects_spec.rb
ADD hyperty_keep_alive_spec.rb /specs/hyperty_keep_alive_spec.rb
ADD data_objects_keep_alive_spec.rb /specs/data_objects_keep_alive_spec.rb

CMD rspec hyperties_spec.rb && rspec data_objects_spec.rb && rspec hyperty_keep_alive_spec.rb && rspec data_objects_keep_alive_spec.rb

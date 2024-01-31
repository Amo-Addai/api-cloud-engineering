module Sluggable
  extend ActiveSupport::Concern

  included do
    extend FriendlyId

    def self.friendly_find(id)
      friendly.find id
    rescue ActiveRecord::RecordNotFound
      nil
    end

    def self.friendly_find!(id)
      friendly.find id
    end
  end
end

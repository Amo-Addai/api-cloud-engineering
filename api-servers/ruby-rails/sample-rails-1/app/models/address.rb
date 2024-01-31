class Address < ApplicationRecord
  belongs_to :item, optional: false
end

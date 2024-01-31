class Category < ApplicationRecord
  include Listable
  include Sluggable
  friendly_id :slug_candidates, use: :slugged

  has_and_belongs_to_many :items

  has_many :subtypes, dependent: :destroy, class_name: 'Category', foreign_key: :parent_id, inverse_of: :parent
  belongs_to :parent, class_name: 'Category', foreign_key: :parent_id, inverse_of: :subtypes, optional: true

  default_scope { order(position: :asc) }
  scope :parents, -> { where(parent_id: nil) }
  scope :active, -> { where.not(hidden: true) }
  scope :subtypes_for, ->(parent_id) { where(parent_id: parent_id) }

  private
    def slug_candidates
      [:name, %i[name id]]
    end
end

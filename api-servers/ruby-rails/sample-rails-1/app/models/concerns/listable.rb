module Listable
  extend ActiveSupport::Concern

  included do
    # Gets all the elements that need to be reordered.
    #   Basically gets all the elements that share the same parent.
    # args
    #   obj: the object to operate on
    #   initial_query: the query used to help descriminate which elements to reorder
    def self.get_elements_to_reorder(obj, initial_query)
      results = initial_query
      if self.method_defined? :parent_id
        # if all share the same parent
        results = results.where(parent_id: obj.parent_id)
        all_results = where(parent_id: obj.parent_id)
      else
        results = all
        all_results = all
      end
      {
        results: results.order(position: :asc),
        all_results: all_results.order(position: :asc)
      }
    end

    def self.delete(obj)
      results, all_results = self.get_elements_to_reorder(obj, self.where('position > ?', obj.position)).values_at(:results, :all_results)

      # set new position
      self.transaction do
        results.each do |result|
          result.position = result.position - 1
          result.save!
        end
        obj.destroy!
      end
      {
        obj: obj,
        results: all_results
      }
    rescue StandardError => e
      Rails.logger.error "Unable to delete: #{e}"
      false
    end

    def self.insert_at(obj, new_position)
      new_position ||= 0
      results, all_results = self.get_elements_to_reorder(obj, self.where('position >= ?', new_position)).values_at(:results, :all_results)

      # set new position
      self.transaction do
        results.each do |result|
          result.position = result.position + 1
          result.save!
        end
        obj.position = new_position
        obj.save!
      end
      {
        obj: obj,
        results: all_results
      }
    rescue StandardError => e
      Rails.logger.error "Unable to insert: #{e}"
      false
    end

    def self.update_at(obj, new_position = nil)
      old_position = obj.position || 0
      # ignore the new element coming in
      results, all_results = self.get_elements_to_reorder(obj, self.where.not(id: obj.id)).values_at(:results, :all_results)

      # save and return if not changing position, requires results of matching same category
      if new_position.blank? || new_position == obj.position
        obj.save!
        return {
          obj: obj,
          results: all_results
        }
      end

      # find elements to shift
      if (old_position > new_position)
        results = results.where('position >= ?', new_position).where('position <= ?', old_position)
        moving_down = true
      elsif (old_position < new_position)
        results = results.where('position <= ?', new_position).where('position >= ?', old_position)
        moving_down = false
      end

      # set new position
      self.transaction do
        obj.position = new_position
        obj.save!

        # boundary from new_position to the obj.position
        results.each do |result|
          result.position = (moving_down) ? (result.position + 1) : (result.position - 1)
          result.save!
        end
      end
      {
        obj: obj,
        results: all_results
      }
    rescue StandardError => e
      Rails.logger.error "Unable to update: #{e}"
      false
    end
  end
end

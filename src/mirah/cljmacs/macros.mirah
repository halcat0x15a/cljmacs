class Dummy
end

interface Macros do
  macro def attr_accessor(name_node, type)
    quote do
      attr_reader `name_node`
      attr_writer `name_node`, `type`
    end
  end

  macro def attr_reader(name_node)
    name = name_node.string_value
    quote do
      def `name`
        @`name`
      end
    end
  end

  macro def attr_writer(name_node, type)
    name = name_node.string_value
    quote do
      def `"#{name}_set"`(value:`type`)
        @`name` = value
      end
    end
  end
end

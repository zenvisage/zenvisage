/*!
 *
 *	jQuery Dropdown
 *
 *	http://danewilliams.uk/projects/jquery-dropdown
 *	https://github.com/daneWilliams/jquery.dropdown
 *
 *	================================================================
 *
 *	@version		1.6.0
 *
 *	@author			Dane Williams <dane@danewilliams.uk>
 *	@copyright		2014-2015 Dane Williams
 *	@license		MIT License
 *
 */


;(function( $, window, document, undefined ) {


	/**
	 *
	 *	Plugin constructor
	 *
	 *	================================================================ */

	dropdown = function( elem, options ) {

		var self = this;

		// Store reference to the element
		self.elem  = elem;
		self.$elem = $(elem);

		// Instance
		self.instance = {

			uid: null,

			items: {},
			menus: {},

			menu: {
				current: null,
				main: null
			},

			above: false,
			open: false,

			opening: false,
			closing: false,
			animating: false,
			resizing: false,
			resetting: false,

			selected: null,
			focused: null,
			value: null,

			resizeTimeout: null

		};

		// Elements
		self.elements = {};

		// Options
		self.options  = options;
		self.metadata = self.$elem.data('dropdown');

		// Initialise
		self.init();

	};


	/**
	 *
	 *	Plugin prototype
	 *
	 *	================================================================ */

	dropdown.prototype = {


		/**
		 *
		 *	Initialise
		 *
		 *	================================================================ */

		init: function() {

			var self = this;

			// Update the options
			self.options   = $.extend( true, {}, self.defaults, self.options, self.metadata );
			self.templates = $.extend( true, {}, self.templates, self.options.templates );
			self.classes   = self._mergeClasses();

			// Check for transition support
			var s = document.createElement('p').style,
			        supportsTransitions = 'transition' in s ||
					'WebkitTransition' in s ||
					'MozTransition' in s ||
					'msTransition' in s ||
					'OTransition' in s;

			if ( !supportsTransitions ) {

				// Disable animation
				self.options.animate = false;

			}

			// Build the dropdown
			self._buildDropdown();

			// Populate the dropdown
			self._populate();

			// Bind events
			self._bindEvents();

			// Callback
			self.$elem.trigger( 'dropdown-init', self );

		},


		/**
		 *
		 *	Open the dropdown
		 *
		 *	================================================================ */

		open: function( menu ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Already opening or closing, bail
			if ( inst.opening || inst.closing )
				return;

			// Open a menu
			if ( menu ) {

				self.openMenu( menu );
				return;

			}

			// Callback
			self._beforeOpen();

			// No animation
			if ( !opt.animate ) {

				// Callback
				self._afterOpen();
				return;

			}

			// Set start values
			var start = {
				opacity: 0,
				y: -( elem.toggleButton.outerHeight() / 2 )
			};

			// Set finish values
			var finish = {
				opacity: 1,
				y: 0
			};

			// Above?
			if ( inst.above ) {

				start.y = ( elem.toggleButton.outerHeight() / 2 );

			} 

			// Mobile?
			var mobile = ( elem.menuWrapper.css( 'position' ) == 'fixed' ? true : false );

			if ( mobile ) {

				start = {
					opacity: 0
				};

				finish = {
					opacity: 1
				};

				if ( $(window).width() > $(window).height() ) {

					start.y  = '100%';
					finish.y = 0;

				} else {

					start.x  = '100%';
					finish.x = 0;

				}

			}

			// Update state
			inst.animating = true;

			// Update classes
			elem.dropdown.addClass( cls.animating );

			// Animate
			elem.menuWrapper.show().css( start );

			if ( mobile ) {

				elem.overlay.show().css( { opacity: 0 } ).transition( { opacity: 1 }, opt.speed );

			}

			elem.menuWrapper.transition( finish, opt.speed, function() {

				// Update state
				inst.animating = false;

				// Update classes
				elem.dropdown.removeClass( cls.animating );

				// Callback
				self._afterOpen();

			});

		},


		/**
		 *
		 *	Close the dropdown
		 *
		 *	================================================================ */

		close: function( menu ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Already closed, closing or opening, bail
			if ( !inst.open || inst.closing || inst.opening )
				return;

			// Close a menu
			if ( menu ) {

				self.closeMenu( menu );
				return;

			}

			// Callback
			self._beforeClose();

			// No animation
			if ( !opt.animate ) {

				// Callback
				self._afterClose();
				return;

			}

			// Set start values
			var start = {
				opacity: 1,
				y: 0
			};

			// Set finish values
			var finish = {
				opacity: 0,
				y: -( elem.toggleButton.outerHeight() / 2 )
			};

			// Above?
			if ( inst.above ) {

				finish.y = ( elem.toggleButton.outerHeight() / 2 );

			} 

			// Mobile?
			var mobile = ( elem.menuWrapper.css( 'position' ) == 'fixed' ? true : false );

			if ( mobile ) {

				start = {
					opacity: 1
				};

				finish = {
					opacity: 0
				};

				if ( $(window).width() > $(window).height() ) {

					start.y  = 0;
					finish.y = '100%';

				} else {

					start.x  = 0;
					finish.x = '100%';

				}

			}

			// Update state
			inst.animating = true;

			// Update classes
			elem.dropdown.addClass( cls.animating );

			// Animate
			elem.menuWrapper.show().css( start );

			if ( mobile ) {

				elem.overlay.transition( { opacity: 0 }, opt.speed );

			}

			elem.menuWrapper.transition( finish, opt.speed, function() {

				// Update state
				inst.animating = false;

				// Update classes
				elem.dropdown.removeClass( cls.animating );

				// Hide the menu
				elem.menuWrapper.hide();

				// Callback
				self._afterClose();

			});

		},


		/**
		 *
		 *	Open a menu
		 *
		 *	================================================================ */

		openMenu: function( menu, noAnimation ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Already opening, bail
			if ( inst.opening )
				return;

			// Get the menu
			menu = self.getMenu( menu );

			// Get the current menu
			var current = self.getMenu();

			// Callback
			self._beforeOpenMenu( menu, current );

			// No animation
			if ( noAnimation || !opt.animate || menu.uid == current.uid ) {

				// Callback
				self._afterOpenMenu( menu, current );
				return;

			}

			// Set start values
			var start = {
				x: '100%'
			};

			menu.elem.show().css( start );
			current.elem.css({ x: 0 });

			// Set finish values
			var finish = {
				x: 0
			};

			// Update state
			inst.animating = true;

			// Update classes
			elem.dropdown.addClass( cls.animating );

			// Animate
			current.elem.transition({ x: '-100%' }, opt.speed );
			menu.elem.transition( finish, opt.speed, function() {

				// Update state
				inst.animating = false;

				// Update classes
				elem.dropdown.removeClass( cls.animating );

				// Callback
				self._afterOpenMenu( menu, current );

			});

		},


		/**
		 *
		 *	Close a menu
		 *
		 *	================================================================ */

		closeMenu: function( menu ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Already closing, bail
			if ( inst.closing )
				return;

			// Get the menu
			menu = self.getMenu( menu );

			// Get the target menu
			var target = false;

			if ( menu.parent ) {

				var item   = self.getItem( menu.parent );
				var target = self.getMenu( item.menu );

			}

			// Callback
			self._beforeCloseMenu( menu, target );

			// No animation
			if ( !opt.animate || !target ) {

				if ( !target )
					self.close();

				// Callback
				self._afterCloseMenu( menu, target );
				return;

			}

			// Set start values
			var start = {
				x: 0
			};

			menu.elem.css( start );
			target.elem.show().css({ x: '-100%' });

			// Set finish values
			var finish = {
				x: '100%'
			};

			// Update state
			inst.animating = true;

			// Update classes
			elem.dropdown.addClass( cls.animating );

			// Animate
			target.elem.transition({ x: 0 }, opt.speed );
			menu.elem.transition( finish, opt.speed, function() {

				// Update state
				inst.animating = false;

				// Update classes
				elem.dropdown.removeClass( cls.animating );

				// Callback
				self._afterCloseMenu( menu, target );

			});

		},


		/**
		 *
		 *	Resize the dropdown
		 *
		 *	================================================================ */

		resize: function( menu, noAnimation ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    obj  = self.objects,
			    elem = self.elements,
			    cls  = self.classes;

			// Already resizing, bail
			if ( inst.resizing )
				return;

			var animate = ( noAnimation ? false : ( menu ? true : false ) );

			// Get the menu
			if ( menu )
				menu = self.getMenu( menu );

			// Menu doesn't exist, get current one
			if ( !menu )
				menu = self.getMenu();

			// Get values
			var resize = $.extend( true, {}, obj.resize );

			// Callback
			self._beforeResize( menu, resize );

			// Window dimensions
			resize.height.viewport = $(window).height();
			resize.width.viewport  = $(window).width();

			// Show the dropdown if needed
			if ( !inst.open ) {

				elem.menuWrapper.show().css({ opacity: 0 });

			}

			// Wrapper dimensions
			resize.height.wrapper = elem.menuWrapper.outerHeight(true);
			resize.width.wrapper  = elem.menuWrapper.outerWidth(true);

			resize.height.diff = resize.height.wrapper - elem.menuWrapper.height();
			resize.width.diff  = resize.width.wrapper - elem.menuWrapper.width();

			// Show the menu
			menu.elem.show().css({ opacity: 0, position: 'fixed', height: '', width: '' });

			// List dimensions
			var $list = menu.elem.children( '.' + cls.core.menuList ).eq(0);

			$list.css({ height: '', width: '' });

			resize.height.list = $list.height();
			resize.width.list  = $list.width();

			// Menu dimensions
			resize.height.menu = menu.elem.outerHeight(true);
			resize.width.menu  = menu.elem.outerWidth(true);

			// Get collision values
			var collision = self._collisionValues( menu, resize );

			// Reset
			if ( !inst.open ) {

				elem.menuWrapper.css({ display: '', opacity: '' });

			}

			menu.elem.css({ display: '', opacity: '', position: '' });

			$list.css({ height: resize.collision.height.list });

			// No animation
			if ( !animate || !opt.animate ) {

				elem.menuWrapper.css({ height: resize.collision.height.menu });

				// Callback
				self._afterResize( menu, resize );
				return resize;

			}

			// Animate
			elem.menuWrapper.transition({ height: resize.collision.height.menu }, opt.speed, function() {

				// Callback
				self._afterResize( menu, resize );

			});

			return resize;

		},


		/**
		 *
		 *	Reset the dropdown
		 *
		 *	================================================================ */

		reset: function( clear ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Get the menus
			var target  = self.getMenu( 'default' );
			var current = self.getMenu();

			// Callback
			self._beforeReset( clear, target, current );

			// Callback
			self._afterReset( clear, target, current );

		},


		/**
		 *
		 *	Select an item or menu
		 *
		 *	================================================================ */

		select: function( item ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Get the item
			item = self.getItem( item );

			// Get a menu
			if ( !item ) {

				var menu = self.getMenu( item );

				// Open menu
				if ( menu )
					self.openMenu( menu );

				return;

			}

			// Parent, open menu
			if ( opt.nested && item.selectable && item.children.menu ) {

				self.openMenu( item.children.menu );
				return;

			}

			// Not selectable
			if ( !item.selectable ) {

				// Link?
				if ( item.href )
					window.location.href = item.href;

				return; 

			}

			// Get currently selected item
			var previous = false;

			if ( !opt.multiple )
				previous = self.getItem( inst.selected );

			if ( previous.uid == item.uid )
				previous = false;

			// Callback
			self._beforeSelect( item, previous );

			// Select an item
			self.selectItem( item, previous );

			// Select/deselect previous
			if ( previous ) {

				if ( !opt.multiple ) {

					previous.selected = false;
					previous.elem.removeClass( cls.selected );

				}

				// Select/deselect previous parent
				self.selectParent( previous );

			}

			// Update toggle text
			if ( opt.autoToggle ) {

				if ( !inst.selected || !inst.selected.length ) {

					if ( opt.multiple )
						self.toggleTextMulti();

					else
						self.toggleText();

				} else {

					if ( opt.multiple )
						self.toggleTextMulti( item.text );

					else
						self.toggleText( item.text );

				}

			}

			// Close the dropdown
			if ( !opt.multiple ) {

				self.close();

			}

			// Callback
			self._afterSelect( item, previous );

		},


		/**
		 *
		 *	Select an item
		 *
		 *	================================================================ */

		selectItem: function( item ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			// Get the item
			item = self.getItem( item );

			if ( !item )
				return;

			if ( opt.multiple ) {

				if ( !inst.selected )
					inst.selected = [];

				if ( item.selected ) {

					// Deselect
					self.deselect( item );

				} else {

					// Select
					item.selected = true;
					item.elem.addClass( cls.selected );

					inst.selected.push( item.uid );

					// Add value
					if ( item.value != null ) {

						if ( inst.value == null )
							inst.value = [];

						inst.value.push( item.value );

					}

				}

			} else {

				// Select
				inst.selected = item.uid;

				item.selected = true;
				item.elem.addClass( cls.selected );

				// Update value
				inst.value = item.value;

			}

			// Select/deselect parent
			self.selectParent( item );

		},


		/**
		 *
		 *	Select item(s) by value(s)
		 *
		 *	================================================================ */

		selectValue: function( values, clear ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    elem = self.elements,
			    cls  = self.classes;

			if ( !values )
				values = [];

			if ( ! ( values instanceof Array ) )
				values = [ values ];

			// Deselect all
			if ( clear ) {

				for ( var uid in inst.items )
					self.deselect( uid );

			}

			// Multiple
			if ( opt.multiple ) {

				// Deselect all
				if ( clear ) {

					if ( opt.autoToggle )
						self.toggleTextMulti();

				}

				if ( !values.length )
					return;

				$.each( values, function( i, value ) {

					for ( var uid in inst.items ) {

						if ( self.value( uid ) == value ) {

							self.selectItem( uid );

							if ( opt.autoToggle )
								self.toggleTextMulti( self.text( uid ) );

						}

					}

				});

				return;

			}

			$.each( values, function( i, value ) {

				for ( var uid in inst.items ) {

					if ( self.value( uid ) == value ) {

						self.selectItem( uid );

						if ( opt.autoToggle )
							self.toggleText( self.text( uid ) );

					}

				}

			});

		},


		/**
		 *
		 *	Deselect an item
		 *
		 *	================================================================ */

		deselect: function( item ) {

			var self = this;
			var inst = self.instance,
				opt  = self.options,
				elem = self.elements,
				cls  = self.classes;

			// Get the item
			item = self.getItem( item );

			// No item, bail
			if ( !item )
				return false;

			if ( !item.selected )
				return false;

			item.selected = false;
			item.elem.removeClass( cls.selected );

			if ( inst.selected && opt.multiple ) {

				var index = $.inArray( item.uid, inst.selected );

				if ( index )
					inst.selected.splice( index, 1 );

				// Remove value(s)
				if ( item.value != null ) {

					if ( inst.value == null )
						inst.value = [];

					inst.value = jQuery.grep( inst.value, function(value) {
						return value != item.value;
					});

				}

			} else {

				if ( item.value == inst.value )
					inst.value = null;

			}

		},


		/**
		 *
		 *	Select a parent item
		 *
		 *	================================================================ */

		selectParent: function( item ) {

			var self = this;
			var opt  = self.options,
				elem = self.elements,
				cls  = self.classes;

			// Get the parent
			var parent = self.getItem( item.parent );

			// No parent, bail
			if ( !parent || !opt.nested )
				return false;

			// Update parent
			if ( item.selected ) {

				// Select parent
				parent.selected = true;
				parent.elem.addClass( cls.selected );

			} else {

				var selected = 0;

				$.each( parent.children.items, function( i, uid ) {

					var child = self.getItem( uid );

					if ( child && child.selected ) {

						selected++;

					}

				});

				if ( !selected ) {

					// Deselect parent
					parent.selected = false;
					parent.elem.removeClass( cls.selected );

				}

			}

			// Does this item have a parent too?
			if ( parent.parent ) {

				self.selectParent( parent );

			}

		},


		/**
		 *
		 *	Focus an item
		 *
		 *	================================================================ */

		focus: function( item ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    cls  = self.classes;

			// Remove current focus
			if ( inst.focused ) {

				var focused = self.getItem( inst.focused );
				focused.elem.removeClass( cls.focused );

				inst.focused = null;

			}

			// No item, bail
			if ( !item )
				return;

			// Get the item
			item = self.getItem( item );

			// No item, bail
			if ( !item )
				return;

			// Update classes
			item.elem.addClass( cls.focused );

			// Update state
			inst.focused = item.uid;

		},


		/**
		 *
		 *	Get value
		 *
		 *	================================================================ */

		value: function( item ) {

			var self = this;
			var inst = self.instance;

			if ( !item )
				return inst.value;

			item = self.getItem( item );

			if ( item )
				return item.value;

		},


		/**
		 *
		 *	Get item text
		 *
		 *	================================================================ */

		text: function( item ) {

			var self = this;

			if ( !item )
				return;

			item = self.getItem( item );

			if ( item )
				return item.text;

		},


		/**
		 *
		 *	Get selected item, or check if item is selected
		 *
		 *	================================================================ */

		selected: function( item ) {

			var self = this;
			var inst = self.instance;

			if ( !inst.selected )
				return false;

			if ( !item ) {

				// Multiple items
				if ( inst.selected instanceof Array ) {

					var items = [];

					$.each( inst.selected, function( i, uid ) {

						items.push( self.getItem( uid ) );

					});

					return items;

				}

				// Single item
				return self.getItem( inst.selected );

			}

			// Check if provided item is selected
			item = self.getItem( item );

			if ( !item )
				return false;

			return item.selected;

		},


		/**
		 *
		 *	Get an item
		 *
		 *	================================================================ */

		getItem: function( item ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements;

			// No item, bail
			if ( !item )
				return false;

			// Get by ID
			if ( typeof item == 'string' ) {

				if ( inst.items[ item ] ) {

					return inst.items[ item ];

				} else {

					if ( elem.dropdown.find( '#' + item ).length ) {

						item = elem.dropdown.find( '#' + item );

					} else {

						return false;

					}

				}

			}

			// Get from jQuery object
			if ( item.jquery ) {

				var uid = item.data( 'dropdown-uid' );

				if ( !uid || !inst.items[ uid ] )
					return false;

				return inst.items[ uid ];

			}

			if ( typeof item != 'object' )
				return false;

			return item;

		},


		/**
		 *
		 *	Get a menu
		 *
		 *	================================================================ */

		getMenu: function( menu ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements;

			// Check if this is an item
			var item = self.getItem( menu );

			if ( item ) {

				menu = item.menu;

			}

			// Get current menu
			if ( !menu )
				menu = inst.menu.current;

			// Get default menu
			if ( 'default' == menu )
				menu = inst.menu.main;

			// Get by ID
			if ( typeof menu == 'string' ) {

				if ( inst.menus[ menu ] ) {

					return inst.menus[ menu ];

				} else {

					if ( elem.dropdown.find( '#' + menu ).length ) {

						menu = elem.dropdown.find( '#' + menu );

					} else {

						return false;

					}

				}

			}

			// Get from jQuery object
			if ( menu.jquery ) {

				var uid = menu.data( 'dropdown-uid' );

				if ( !uid || !inst.menus[ uid ] )
					return false;

				return inst.menus[ uid ];

			}

			if ( typeof menu != 'object' )
				return false;

			return menu;

		},


		/**
		 *
		 *	Add an item
		 *
		 *	================================================================ */

		addItem: function( item, menu ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options;

			// Make sure this is an array
			var items = item;
			var added = [];

			// Get the menu
			if ( !opt.nested ) {

				menu = self.getMenu();

			} else {

				menu = self.getMenu( menu );

			}

			// Loop through the items
			$.each( items, function( i, item ) {

				item = $.extend( true, {}, self.objects.item, item );

				// Get unique ID
				if ( !item.uid )
					item.uid = self.getID();

				if ( !item.menu )
					item.menu = menu.uid;

				// Add to plugin
				inst.items[ item.uid ] = item;
				added.push( item );

				// Selected?
				if ( item.selected && !item.children.items ) {

					if ( opt.multiple ) {

						if ( !inst.selected )
							inst.selected = [];

						inst.selected.push( item.uid );

						// Update toggle text
						if ( opt.autoToggle ) {

							self.toggleTextMulti( item.text );

						}

					} else {

						inst.selected = item.uid;

						// Update toggle text
						if ( opt.autoToggle ) {

							self.toggleText( item.text );

						}

					}

				}

				// Any child items?
				if ( item.children.items && item.children.items.length ) {

					// Set menu
					if ( !opt.nested ) {

						item.children.menu = menu;

					} else {

						if ( !item.children.menu ) {

							var submenu = self.addMenu([{ parent: item.uid, title: item.children.title }]);

							item.children.menu = submenu[0].uid;

						}

					}

					// Add parent
					if ( item.value || item.href || opt.selectParents ) {

						var parent = $.extend( {}, item, { 
							uid: false, 
							menu: false,
							parent: item.uid, 
							children: {}, 
							divider: {
								bottom: true
							}
						} );

						if ( !opt.nested )
							parent.divider = {
								top: true
							};

						item.children.items.unshift( parent );

					} else {

						// Add label
						if ( !opt.nested ) {

							if ( !item.children.items[0].label )
								item.children.items[0].label = item.text;

							if ( !item.children.items[0].divider )
								item.children.items[0].divider = { top: false, bottom: false };

							item.children.items[0].divider.top = true;

						} 

					}

					var children = self.addItem( item.children.items, item.children.menu );

					item.children.items = [];

					// Modify child items and parent
					$.each( children, function( j, child ) {

						inst.items[ child.uid ].parent = item.uid;

						item.children.items.push( child.uid );

						if ( child.selected )
							item.selected = true;

					});

					// Add element
					if ( opt.nested )
						item.elem = self._buildItem( item );

				} else {

					// Add element
					item.elem = self._buildItem( item );

				}

			});

			return added;

		},


		/**
		 *
		 *	Add a menu
		 *
		 *	================================================================ */

		addMenu: function( menu ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    cls  = self.classes;

			// Make sure this is an array
			var menus = menu;
			var added = [];

			$.each( menus, function( i, menu ) {

				menu = $.extend( true, {}, self.objects.menu, menu );

				// Get unique ID
				if ( !menu.uid )
					menu.uid = self.getID();

				// Add to plugin
				inst.menus[ menu.uid ] = menu;
				added.push( menu );

				// Set title
				if ( !menu.title ) {

					menu.title = opt.titleText;

					if ( opt.autoTitle && menu.parent ) {

						var parent = self.getItem( menu.parent );

						if ( parent ) {

							menu.title = parent.text;

						}

					}

				}

				// Get element
				menu.elem = self._buildMenu( menu );

				// Any items?
				if ( menu.items ) {

					self.addItem( menu.items, menu.uid );

				}

				// Default?
				if ( !inst.menu.main ) {

					inst.menu.main = menu.uid;
					inst.menu.current = menu.uid;

					menu.open = true;
					menu.elem.addClass( cls.menuOpen );

				}

			});

			return added;

		},


		/**
		 *
		 *	Get unique ID
		 *
		 *	================================================================ */

		getID: function() {

			var id = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
				var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
				return v.toString(16);
			});

			return id;

		},


		/**
		 *
		 *	Set title text
		 *
		 *	================================================================ */

		titleText: function( text, menu ) {

			var self = this;
			var elem = self.elements,
			    cls  = self.classes;

			// Get menu
			menu = self.getMenu( menu );

			// No menu, bail
			if ( !menu )
				return;

			// Store the original
			if ( !menu.elem.data('dropdown-title') )
				menu.elem.data('dropdown-title', $title.html() );

			var $title = menu.elem.find( '.' + cls.core.menuTitle );

			if ( text ) {

				$title.html( text );

			} else {

				$title.html( menu.elem.data('dropdown-title') );

			}

		},


		/**
		 *
		 *	Set toggle text
		 *
		 *	================================================================ */

		toggleText: function( text ) {

			var self = this;
			var elem = self.elements;

			// Store the original
			if ( !elem.toggleButton.data( 'dropdown-text' ) )
				elem.toggleButton.data( 'dropdown-text', elem.toggleText.text() );

			if ( text ) {

				elem.toggleText.html( text );

			} else {

				elem.toggleText.html( elem.toggleButton.data( 'dropdown-text' ) );

			}

		},


		/**
		 *
		 *	Set multiple toggle text
		 *
		 *	================================================================ */

		toggleTextMulti: function( text ) {

			var self = this;
			var elem = self.elements;
			var vals = elem.toggleButton.data( 'dropdown-text-multi' );

			// Store the original
			if ( !elem.toggleButton.data( 'dropdown-text' ) )
				elem.toggleButton.data( 'dropdown-text', elem.toggleText.text() );

			if ( text ) {

				// Check for values
				if ( !vals )
					vals = [];

				// Check if text already exists
				var index = vals.indexOf( text );

				// Text already exists, remove it
				if ( index != -1 ) {

					vals.splice( index, 1 );

				} else {

					vals.push( text );

				}

				// No values
				if ( !vals || !vals.length ) {

					var str = elem.toggleButton.data( 'dropdown-text' );

				} else {

					// Create text string
					var str = vals.join( ', ' );

				}

				// Store values
				elem.toggleButton.data( 'dropdown-text-multi', vals );

				// Update
				elem.toggleText.html( str );

			} else {

				vals = [];

				elem.toggleButton.data( 'dropdown-text-multi', vals );
				elem.toggleText.html( elem.toggleButton.data( 'dropdown-text' ) );

			}

		},


		/**
		 *
		 *	Bind events
		 *
		 *	================================================================ */

		_bindEvents: function() {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
			    cls  = self.classes,
			    elem = self.elements;

			// Toggle
			self.$elem.on( 'dropdown-toggle', function() {

				if ( !inst.open )
					self.open();

				else
					self.close();

			});

			elem.toggleButton.on( 'click', function(e) {

				e.preventDefault();

				self.$elem.trigger( 'dropdown-toggle' );

			});

			// Open dropdown
			elem.dropdown.on( 'open', function() {

				self.open();

			});

			// Close dropdown
			elem.dropdown.on( 'close', function() {

				self.close();

			});

			elem.dropdown.on( 'click', '.' + cls.core.closeButton, function(e) {

				e.preventDefault();

				self.close();

			});

			// Select item
			elem.dropdown.on( 'click', '.' + cls.core.menuItem, function(e) {

				e.preventDefault();

				var item = $(this).data( 'dropdown-uid' );

				self.select( item );

			});

			// Sync with <select />
			self.$elem.on( 'change', function() {

				self.selectValue( self.$elem.val(), true );

			});

			// Close menu
			elem.dropdown.on( 'click', '.' + cls.core.backButton, function(e) {

				e.preventDefault();

				self.closeMenu();

			});

			// Auto close
			if ( opt.autoClose ) {

				$(document).on( 'mousedown', function(e) {

					var $target   = $(e.target);
					var $dropdown = $target.parents( '.' + cls.core.dropdown );

					if ( !$dropdown.length ) {

						$( '.' + cls.core.open ).trigger( 'close' );
						return;

					}

				});

			}

			// Auto resize
			if ( opt.autoResize ) {

				$(window).resize(function() {

					if ( inst.resizeTimeout ) 
						clearTimeout( inst.resizeTimeout );

					inst.resizeTimeout = setTimeout(function() {

						self._autoResize();

					}, opt.autoResize );

				});

			}

			// Keyboard navigation
			if ( opt.keyboard ) {

				$(document).on( 'keypress', function(e) {

					// Ignore this dropdown
					if ( !inst.open && !elem.toggleButton.is(':focus') )
						return;

					// Get the focused item
					var focused = inst.focused;

					if ( focused ) {

						focused = self.getItem( focused );

					}

					// Get the pressed key
					var keyCode = ( e.keyCode ? e.keyCode : e.which );

					switch ( keyCode ) {

						// Tab
						case 9 :

							// Close
							if ( elem.toggleButton.is(':focus') && inst.open ) {

								e.preventDefault();

								self.close();

							}

						break;

						// Enter
						case 13 :

							e.preventDefault();

							// Select an item
							if ( focused ) {

								// Check for menu
								if ( focused.children.menu ) {

									// Get the menu
									var menu = self.getMenu( focused.children.menu );

									// Focus the first or selected item
									var target = menu.elem.find( '.' + cls.core.menuItem );

									if ( menu.elem.find( '.' + cls.core.selected ).length )
										target = menu.elem.find( '.' + cls.core.selected );

									self.focus( target.eq(0) );

									// Open the menu
									self.open( focused.children.menu );

								} else {

									// Select the item
									self.select( focused );

								}

							} else {

								// Toggle dropdown
								if ( elem.toggleButton.is(':focus') ) {

									if ( !inst.open )
										self.open();

									else
										self.close();

								}

							}

						break;

						// Escape
						case 27 :

							// Close dropdown
							if ( inst.open ) {

								e.preventDefault();

								self.close();

							}

						break;	

						// Down
						case 40:

							e.preventDefault();

							var menu = self.getMenu();

							// Open dropdown
							if ( elem.toggleButton.is(':focus') && !inst.open ) {

								self.open();

								// Focus the first or selected item
								var target = menu.elem.find( '.' + cls.core.menuItem );

								if ( menu.elem.find( '.' + cls.core.selected ).length ) {

									target = menu.elem.find( '.' + cls.core.selected );

								}

								self.focus( target.eq(0) );

							} else {

								// Focus the first or selected item
								if ( !focused ) {

									// Focus the first or selected item
									var target = menu.elem.find( '.' + cls.core.menuItem );

									if ( menu.elem.find( '.' + cls.core.selected ).length ) {

										target = menu.elem.find( '.' + cls.core.selected );

										if ( target.next().length )
											target = target.next();

									}

									self.focus( target.eq(0) );

								}

							}

							if ( focused ) {

								// Focus the next item
								if ( focused.elem.next().length ) {

									self.focus( focused.elem.next() );

								}

							}

						break;

						// Left
						case 37 :

							// Close menu
							if ( inst.open && inst.menu.main != inst.menu.current ) {

								e.preventDefault();

								// Get the target item
								var menu = self.getMenu();
								var item = self.getItem( menu.parent );

								// Close the menu
								self.closeMenu();

								// Focus the item
								self.focus( item );

							}

						break;	

						// Up
						case 38:

							if ( inst.open && focused ) {

								e.preventDefault();

								// Defocus
								if ( !focused.elem.prev().length ) {

									self.focus( false );
									elem.toggleButton.focus();

								} else {

									// Focus the previous item
									self.focus( focused.elem.prev() );

								}

							}

						break;

						// Right
						case 39 :

							// Open menu
							if ( inst.open && focused ) {

								if ( focused.children.menu ) {

									e.preventDefault();

									// Get the menu
									var menu = self.getMenu( focused.children.menu );

									// Focus the first or selected item
									var target = menu.elem.find( '.' + cls.core.menuItem );

									if ( menu.elem.find( '.' + cls.core.selected ).length )
										target = menu.elem.find( '.' + cls.core.selected );

									self.focus( target.eq(0) );

									// Open the menu
									self.open( focused.children.menu );

								}

							}

						break;			 

					}


				});

			}

		},


		/**
		 *
		 *	Build the dropdown
		 *
		 *	================================================================ */

		_buildDropdown: function() {

			var self = this;
			var opt  = self.options,
			    cls  = self.classes,
			    tpl  = self.templates,
			    elem = self.elements;

			// Loop through each template
			$.each( self.templates, function( name, tpl ) {

				elem[ name ] = $( tpl );

				// Add classes
				if ( cls[ name ] )
					elem[ name ].addClass( cls[ name ] );

			});

			// Add unique ID
			var uid = self.getID();

			self.instance.uid = uid;
			elem.dropdown.data( 'dropdown-uid', uid );

			// Build the structure
			elem.overlay.appendTo( elem.dropdown );

			elem.menuWrapper.appendTo( elem.dropdown );
			elem.menuContainer.appendTo( elem.menuWrapper );

			elem.menuMask.prependTo( elem.menuWrapper );

			// Toggle button
			var toggleButton = ( opt.toggleElem.button ? $( opt.toggleElem.button ) : false );

			if ( toggleButton && !toggleButton.length ) {

				opt.toggleElem.button = false;
				toggleButton = false;

			}

			if ( toggleButton ) {

				elem.toggleButton = toggleButton;

			} else {

				elem.toggleIcon.appendTo( elem.toggleButton );
				elem.toggleText.appendTo( elem.toggleButton );

			}

			elem.toggleButton.eq(0).appendTo( elem.dropdown );

			// Toggle text
			var toggleText = ( opt.toggleElem.text ? $( opt.toggleElem.text ) : false );

			if ( toggleText && !toggleText.length ) {

				opt.toggleElem.text = false;
				toggleText = false;

			}

			if ( toggleText ) {

				elem.toggleText = toggleText;

			} else {

				if ( toggleButton ) {

					elem.toggleText = elem.toggleButton;

				}

			}

			// Set toggle text
			elem.toggleButton.data( 'dropdown-text', opt.toggleText )

			elem.toggleText.html( opt.toggleText );

			// Add to plugin
			self.elements = elem;

			// Add to page
			self.$elem.hide().after( elem.dropdown );

			// ID?
			if ( self.$elem.attr('id') ) {

				elem.dropdown.attr( 'id', self.$elem.attr('id') + '-dropdown' );

			}

			// Add default menu
			self.addMenu( [{}] );

		},


		/**
		 *
		 *	Build an item
		 *
		 *	================================================================ */


		_buildItem: function( item ) {

			var self = this;
			var cls  = self.classes,
			    tpl  = self.templates,
			    elem = self.elements;

			// Get the menu
			var menu = self.getMenu( item.menu );

			// No menu, bail
			if ( !menu )
				return;

			// Create elements
			var $item = $( tpl.menuItem ).addClass( cls.menuItem );

			if ( item.html ) {

				$( item.html ).appendTo( $item );

			} else {

				var $link = $( tpl.menuLink ).addClass( cls.menuLink ).appendTo( $item );
				var $text = $( tpl.menuText ).addClass( cls.menuText ).appendTo( $link );

				// Set href
				if ( item.href ) {

					$link.attr( 'href', item.href );

				}

				// Set text
				$text.html( item.text );

				// Add icon
				if ( item.children.items ) {

					var $icon = $( tpl.iconNext ).addClass( cls.iconNext ).prependTo( $link );

				}

			}

			// Set ID
			$item.data( 'dropdown-uid', item.uid );

			if ( item.id ) {

				$item.attr( 'id', item.id );

			}

			// Selected?
			if ( item.selected ) {

				$item.addClass( cls.selected );

			}

			// Add top divider
			if ( item.divider.top || true == item.divider ) {

				var $dividerTop = $( tpl.menuDivider ).addClass( cls.menuDivider );

				menu.elem.children( '.' + cls.core.menuList ).append( $dividerTop );

			}

			// Add label
			if ( item.label ) {

				var $label     = $( tpl.menuLabel ).addClass( cls.menuLabel );
				var $labelText = $( tpl.menuText ).addClass( cls.menuText ).appendTo( $label );

				$labelText.html( item.label );

				menu.elem.children( '.' + cls.core.menuList ).append( $label );

			}

			// Add to menu
			menu.elem.children( '.' + cls.core.menuList ).append( $item );

			// Add bottom divider
			if ( item.divider.bottom || true == item.divider ) {

				var $dividerBottom = $( tpl.menuDivider ).addClass( cls.menuDivider );

				menu.elem.children( '.' + cls.core.menuList ).append( $dividerBottom );

			}

			return $item;

		},


		/**
		 *
		 *	Build a menu
		 *
		 *	================================================================ */

		_buildMenu: function( menu ) {

			var self = this;
			var opt  = self.options,
			    cls  = self.classes,
			    tpl  = self.templates,
			    elem = self.elements;

			// Create elements
			var $menu = $( tpl.menuObject ).clone().addClass( cls.menuObject );

			var $header = $( tpl.menuHeader ).addClass( cls.menuHeader ).appendTo( $menu );
			var $title  = $( tpl.menuTitle ).addClass( cls.menuTitle ).appendTo( $header );

			var $close = $( tpl.closeButton ).addClass( cls.closeButton ).appendTo( $header );
			var $back  = $( tpl.backButton ).addClass( cls.backButton ).prependTo( $header );

			var $closeIcon = $( tpl.closeIcon ).addClass( cls.closeIcon ).appendTo( $close );
			var $closeText = $( tpl.closeText ).addClass( cls.closeText ).appendTo( $close );

			var $backIcon = $( tpl.backIcon ).addClass( cls.backIcon ).appendTo( $back );
			var $backText = $( tpl.backText ).addClass( cls.backText ).appendTo( $back );

			var $list = $( tpl.menuList ).addClass( cls.menuList ).appendTo( $menu );

			// Add child classes
			if ( menu.parent ) {

				$menu.addClass( cls.menuChild );

			}

			// Set title text
			$title.html( menu.title );

			// Add button text
			$closeText.html( opt.closeText );
			$backText.html( opt.backText );

			// Set ID
			$menu.data( 'dropdown-uid', menu.uid );

			if ( menu.id ) {

				$menu.attr( 'id', menu.id );

			}

			// Add to dropdown
			elem.menuContainer.append( $menu );

			return $menu;

		},


		/**
		 *
		 *	Populate the dropdown
		 *
		 *	================================================================ */

		_populate: function() {

			var self = this;

			// No children, bail
			if ( !self.$elem.children().length )
				return;

			// Get the tagname
			var tag = self.$elem.prop('tagName');

			// Form select
			if ( tag == 'SELECT' ) {

				// Multiple?
				if ( self.$elem.attr( 'multiple' ) )
					self.options.multiple = true;

				self._populateSelect();
				return;

			}

			// List
			if ( tag == 'UL' || tag == 'OL' ) {

				self._populateList();
				return;

			}

		},


		/**
		 *
		 *	Populate from form select
		 *
		 *	================================================================ */

		_populateSelect: function( $parent ) {

			var self = this;

			var items   = [];
			var $target = ( $parent ? $parent : self.$elem );

			$target.children().each(function() {

				var $this = $(this);

				var item = {
					uid:   self.getID(),
					text:  '',
					value: null,
					children: {
						items: false
					}
				};

				if ( 'OPTGROUP' == $this.prop('tagName') ) {

					item.text = $this.attr('label');

					var children = self._populateSelect( $this );

					item.children.items = [];

					// Add children
					$.each( children, function( i, child ) {

						item.children.items.push( $.extend( {}, child, { parent: item.uid } ) );

					});

				} else {

					item.text  = $this.text();
					item.value = $this.attr('value');

					// Default to text value
					if ( !item.value && item.value !== '0' )
						item.value = item.text;

					// Selected?
					if ( $this.is(':selected') )
						item.selected = true;

				}

				// Add to items
				items.push( item );

			});

			// Return child items
			if ( $parent )
				return items;

			// Add to dropdown
			self.addItem( items );

		},


		/**
		 *
		 *	Populate from ordered or unordered list
		 *
		 *	================================================================ */

		_populateList: function( $parent ) {

			var self = this;
			var cls  = self.classes;

			var items   = [];
			var $target = ( $parent ? $parent : self.$elem );

			$target.children().each(function() {

				var $this = $(this);

				var item = {
					text:  '',
					value: null,
					children: {
						items: false
					}
				};

				item = $.extend( {}, item, $this.data('dropdown') );

				if ( !item.uid )
					item.uid = self.getID();

				// Get child items
				if ( $this.children('ul, ol').length ) {

					var children = self._populateList( $this.children('ul, ol') );

					item.children.items = []

					$.each( children, function( i, child ) {

						item.children.items.push( $.extend( {}, child, { parent: item.uid } ) );

					});

				} else {

					if ( !item.text ) {

						item.text = $this.html();

					}

				}

				// Check for text
				if ( $this.data('dropdown-text') ) {

					item.text = $this.data('dropdown-text');

				}

				if ( $this.children('span').length ) {

					var $text = $this.children('span');

					if ( !$this.data('dropdown-text') ) {

						item.text = $text.html();

					} else {

						if ( !item.header )
							item.header = $text.html();

					}

				}

				// Check for link
				if ( $this.children('a').length ) {

					var $link = $this.children('a');

					item.href = $link.attr('href');
					item.text = $link.html();

				}

				// Selected?
				if ( $this.hasClass( cls.core.selected ) )
					item.selected = true;

				// Add to items
				items.push( item );

			});

			// Return child items
			if ( $parent )
				return items;

			// Add to dropdown
			self.addItem( items );

		},


		/**
		 *
		 *	Called before the dropdown is opened
		 *
		 *	================================================================ */

		_beforeOpen: function() {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.opening = true;

			// Update class to allow page style changes before opening
			$('html').addClass( cls.pageOpenBefore );

			// Resize
			var resize = self.resize( false, true );

			// Set position
			if ( resize.collision.position.y == 'top' ) {

				elem.dropdown.removeClass( cls.below );
				elem.dropdown.addClass( cls.above );

				inst.above = true;

			} else {

				elem.dropdown.removeClass( cls.above );
				elem.dropdown.addClass( cls.below );

				inst.above = false;

			}

			// Scroll to selected item
			self._scrollSelected( false, resize );

			// Update classes
			elem.dropdown.addClass( cls.opening );

			$('html').removeClass( cls.pageOpenBefore ).addClass( cls.pageOpening );

			// Close any other dropdowns
			$( '.' + cls.core.open ).trigger( 'close' );

			// Event
			self.$elem.trigger( 'dropdown-before-open', self );

		},


		/**
		 *
		 *	Called after the dropdown is opened
		 *
		 *	================================================================ */

		_afterOpen: function() {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.opening = false;
			inst.open = true;

			// Update classes
			elem.dropdown.removeClass( cls.opening );
			elem.dropdown.addClass( cls.open );

			$('html').removeClass( cls.pageOpening ).addClass( cls.pageOpen );

			// Focus the toggle button
			elem.toggleButton.focus();

			// Event
			self.$elem.trigger( 'dropdown-after-open', self );

		},


		/**
		 *
		 *	Called before the dropdown is closed
		 *
		 *	================================================================ */

		_beforeClose: function() {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.closing = true;

			// Update classes
			elem.dropdown.addClass( cls.closing );

			$('html').addClass( cls.pageClosing );

			// Defocus
			self.focus( false );

			// Event
			self.$elem.trigger( 'dropdown-before-close', self );

		},


		/**
		 *
		 *	Called after the dropdown is closed
		 *
		 *	================================================================ */

		_afterClose: function() {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			setTimeout(function() {

				inst.closing = false;
				inst.open = false;

			}, 1 );

			// Update classes
			elem.dropdown.removeClass( cls.closing );
			elem.dropdown.removeClass( cls.open );

			$('html').removeClass( cls.pageClosing ).removeClass( cls.pageOpen );

			// Reset overlay
			elem.overlay.css({ display: '', opacity: '' });

			// Reset menus
			self.reset();

			// Reset dimensions
			elem.menuWrapper.css({ height: '' });

			// Event
			self.$elem.trigger( 'dropdown-after-close', self );

		},


		/**
		 *
		 *	Called before a menu is opened
		 *
		 *	================================================================ */

		_beforeOpenMenu: function( target, current ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Resize
			var resize = self.resize( target.uid );

			// Update state
			inst.opening = true;

			// Update classes
			target.elem.addClass( cls.menuOpening );
			current.elem.addClass( cls.menuClosing );

			// Scroll to selected item
			self._scrollSelected( target.uid, resize );

			// Event
			self.$elem.trigger( 'dropdown-before-open-menu', [ target, current, self ] );

		},


		/**
		 *
		 *	Called after a menu is opened
		 *
		 *	================================================================ */

		_afterOpenMenu: function( target, current ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.opening = false;
			current.open = false;
			target.open  = true;

			// Update plugin
			inst.menu.current = target.uid;

			// Update classes
			target.elem.removeClass( cls.menuOpening );
			current.elem.removeClass( cls.menuClosing );

			current.elem.removeClass( cls.menuOpen );
			target.elem.addClass( cls.menuOpen );

			// Reset dimensions
			current.elem.find( '.' + cls.core.menuList ).eq(0).css({ height: '' });

			// Event
			self.$elem.trigger( 'dropdown-after-open-menu', [ target, current, self ] );

		},


		/**
		 *
		 *	Called before a menu is closed
		 *
		 *	================================================================ */

		_beforeCloseMenu: function( current, target ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Resize
			if ( target ) {

				self.resize( target.uid );

			}

			// Update state
			inst.closing = true;

			// Update classes
			current.elem.addClass( cls.menuClosing );

			if ( target ) {

				target.elem.addClass( cls.menuOpening );

			}

			// Event
			self.$elem.trigger( 'dropdown-before-close-menu', [ current, target, self ] );

		},


		/**
		 *
		 *	Called after a menu is closed
		 *
		 *	================================================================ */

		_afterCloseMenu: function( current, target ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.closing = false;
			current.open = false;

			if ( target ) {

				target.open = true;

			}

			// Update plugin
			if ( target ) {

				inst.menu.current = target.uid;

			} else {

				inst.menu.current = inst.menu.main;

			}

			// Update classes
			current.elem.removeClass( cls.menuClosing );
			current.elem.removeClass( cls.menuOpen );

			if ( target ) {

				target.elem.removeClass( cls.menuOpening );
				target.elem.addClass( cls.menuOpen );

			}

			// Reset dimensions
			current.elem.find( '.' + cls.core.menuList ).eq(0).css({ height: '' });

			// Event
			self.$elem.trigger( 'dropdown-after-close-menu', [ current, target, self ] );

		},


		/**
		 *
		 *	Called before the dropdown is resized
		 *
		 *	================================================================ */

		_beforeResize: function( menu, resize ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.resizing = true;

			// Event
			self.$elem.trigger( 'dropdown-before-resize', [ menu, resize, self ] );

		},


		/**
		 *
		 *	Called after the dropdown is resized
		 *
		 *	================================================================ */

		_afterResize: function( menu, resize ) {

			var self = this;
			var inst = self.instance;

			// Update state
			inst.resizing = false;

			// Event
			self.$elem.trigger( 'dropdown-after-resize', [ menu, resize, self ] );

		},


		/**
		 *
		 *	Called before the dropdown is resized
		 *
		 *	================================================================ */

		_beforeReset: function( clear, target, current ) {

			var self = this;
			var inst = self.instance;

			// Update state
			inst.resetting = true;

			// Event
			self.$elem.trigger( 'dropdown-before-reset', [ clear, target, current, self ] );

		},


		/**
		 *
		 *	Called before the dropdown is reset
		 *
		 *	================================================================ */

		_afterReset: function( clear, target, current ) {

			var self = this;
			var inst = self.instance,
			    elem = self.elements,
			    cls  = self.classes;

			// Update state
			inst.resetting = false;
			inst.opening   = false;
			inst.closing   = false;
			inst.animating = false;

			current.open   = false;
			target.open    = true;

			// Update plugin
			inst.menu.current = target.uid;

			// Update classes
			target.elem.removeClass( cls.menuOpening );
			current.elem.removeClass( cls.menuClosing );

			current.elem.removeClass( cls.menuOpen );
			target.elem.addClass( cls.menuOpen );

			// Update positions
			elem.menuWrapper.css({ x: 0, y: 0 });

			current.elem.css({ x: '-100%' });
			target.elem.css({ x: 0 });

			// Reset dimensions
			elem.menuWrapper.css({ height: '' });
			current.elem.find( '.' + cls.core.menuList ).eq(0).css({ height: '' });

			// Event
			self.$elem.trigger( 'dropdown-after-reset', [ clear, target, current, self ] );

		},


		/**
		 *
		 *	Auto resize
		 *
		 *	================================================================ */

		_autoResize: function() {

			var self = this;
			var inst = self.instance;

			if ( inst.open ) {

				self.resize( false, true );

			}

		},


		/**
		 *
		 *	Get resize collision values
		 *
		 *	================================================================ */

		_collisionValues: function( menu, resize ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
				elem = self.elements,
				cls  = self.classes;

			// Get scroll distances
			var scrolled = {
				x: $(document).scrollLeft(),
				y: $(document).scrollTop()
			};

			// Get position
			var position = {
				x: 'left',
				y: 'bottom'
			};

			var offset = {
				x: elem.dropdown.offset().left,
				y: elem.dropdown.offset().top
			};

			// Get available space
			var space = {
				top:    ( offset.y - scrolled.y ),
				bottom: ( $(window).height() + scrolled.y ) - offset.y - elem.toggleButton.outerHeight(true),
				left:   ( offset.x - scrolled.x ),
				right:  ( $(window).width() + scrolled.x ) - offset.x
			};

			// Account for margin
			$.each( space, function( i, value ) {

				space[ i ] = value - opt.margin;

			});

			// Check for mobile
			var mobile = ( elem.menuWrapper.css('position') == 'fixed' ? true: false );

			// Get new heights
			var height = $.extend( {}, resize.height );

			height.total = ( height.menu + height.diff );

			if ( mobile ) {

				if ( height.menu > height.wrapper ) {

					height.menu = ( height.wrapper - height.diff );

				}

			} else {

				// Exceeds maximum height?
				if ( height.total > space.bottom || ( inst.above && height.total > space.top ) ) {

					height.menu = ( space.bottom - height.diff );

					// More space above?
					if ( space.top > space.bottom ) {

						position.y = 'top';

						if ( height.total > space.top ) {

							height.menu = ( space.top - height.diff );

						} else {

							height.menu = resize.height.menu;

						}

					}

				}

			}

			height.list = height.menu - ( resize.height.menu - resize.height.list );

			// Get new width
			var width = $.extend( {}, resize.width );

			// Add to values
			resize.collision.height   = height;
			resize.collision.width    = width;
			resize.collision.scrolled = scrolled;
			resize.collision.position = position;
			resize.collision.offset   = offset;
			resize.collision.space    = space;

			return resize;

		},


		/**
		 *
		 *	Scroll to selected item
		 *
		 *	================================================================ */

		_scrollSelected: function( menu, resize ) {

			var self = this;
			var inst = self.instance,
			    opt  = self.options,
				elem = self.elements,
				cls  = self.classes;

			// Get the menu
			menu = self.getMenu( menu );

			// No menu, bail
			if ( !menu )
				return;

			// Show the dropdown if needed
			if ( !inst.open ) {

				elem.menuWrapper.show().css({ opacity: 0 });

			}

			// Show the menu if needed
			if ( !menu.open ) {

				menu.elem.show().css({ opacity: 0 });

			}

			// Get list
			var $list = menu.elem.children( '.' + cls.core.menuList ).eq(0);

			// Get selected position
			var selectedOffset = 0;

			var $selected = menu.elem.find( '.' + cls.core.selected ).eq(0);

			if ( $selected.length ) {

				selectedOffset = $selected.position().top;

				if ( selectedOffset < 0 || selectedOffset > resize.collision.height.list ) {

					selectedOffset = selectedOffset + $list.scrollTop();

				}

				selectedOffset = selectedOffset - ( resize.collision.height.menu - resize.collision.height.list );

			}

			// Scroll
			$list.animate( { scrollTop: selectedOffset }, 1 );

			// Reset
			if ( !inst.open ) {

				elem.menuWrapper.css({ display: '', opacity: '' });

			}

			if ( !menu.open ) {

				menu.elem.css({ display: '', opacity: '' });

			}

		},


		/**
		 *
		 *	Called before an item is selected
		 *
		 *	================================================================ */

		_beforeSelect: function( item, previous ) {

			var self = this;

			// Event
			self.$elem.trigger( 'dropdown-before-select', [ item, previous, self ] );

		},


		/**
		 *
		 *	Called after an item is selected
		 *
		 *	================================================================ */

		_afterSelect: function( item, previous ) {

			var self = this;
			var inst = self.instance,
				opt  = self.options;

			// Update <select /> value(s)
			if ( 'SELECT' == self.$elem.prop('tagName') ) {

				self.$elem.val( inst.value );

			}

			// Event
			self.$elem.trigger( 'dropdown-after-select', [ item, previous, self ] );

		},


		/**
		 *
		 *	Merge classes
		 *
		 *	================================================================ */

		_mergeClasses: function() {

			var self = this;

			var user = self.options.classes;
			var core = $.extend( {}, self.classes );

			var classes = {};

			$.each( core, function( i, coreClass ) {

				// Add the core class
				if ( !classes.core )
					classes.core = {};

				classes.core[i] = coreClass;

				var classStr = coreClass;

				// Check for user class
				if ( user[i] ) {

					classStr += ' ';
					classStr += user[i];

				}

				// Add to object
				classes[i] = classStr;

			});

			return classes;

		},


		/**
		 *
		 *	Objects
		 *
		 *	================================================================ */

		objects: {

			item: {

				uid: null,
				id: null,

				text: '',
				value: null,
				href: false,
				html: null,

				selected: false,
				selectable: true,

				menu: false,
				parent: false,

				children: {
					menu: false,
					title: '',
					items: false
				},

				label: false,
				divider: {
					top: false,
					bottom: false
				}

			},

			menu: {

				uid: null,
				id: null,

				title: '',

				open: false,

				parent: false,
				children: false

			},

			resize: {

				width: {
					viewport: 0,
					wrapper: 0,
					diff: 0,
					menu: 0,
					list: 0
				},

				height: {
					viewport: 0,
					wrapper: 0,
					diff: 0,
					menu: 0,
					list: 0
				},

				collision: {

					width: {},
					height: {},

					offset: {
						x: 0,
						y: 0
					},

					position: {
						x: 'left',
						y: 'bottom'
					},

					scrolled: {
						x: 0,
						y: 0
					},

					space: {
						above: 0,
						below: 0,
						left: 0,
						right: 0
					}

				}

			}

		},


		/**
		 *
		 *	Defaults
		 *
		 *	================================================================ */

		defaults: {

			// Animation
			animate: true,
			speed: 300,

			// Auto
			autoClose: true,
			autoToggle: true,
			autoTitle: true,			
			autoResize: 300,

			// Nesting
			nested: true,
			selectParents: false,

			// Multiple
			multiple: false,

			// Keyboard navigation
			keyboard: true,

			// Spacing
			maxHeight: 0,
			maxWidth: 0,
			margin: 30,

			// Text
			toggleText: 'Please select',
			titleText: 'Please select',
			backText: 'Back',
			closeText: 'Close',

			// Custom toggle
			toggleElem: {

				button: null,
				text: null

			},

			// Classes
			classes: {

				// Icons
				toggleIcon: 'dropdown-icon-toggle',

				backIcon: 'dropdown-icon-back',
				closeIcon: 'dropdown-icon-close',

				iconPrev: 'dropdown-icon-prev',
				iconNext: 'dropdown-icon-next'

			},

			// Templates
			templates: {}

		},


		/**
		 *
		 *	HTML templates
		 *
		 *	================================================================ */

		templates: {

			dropdown:      '<div />',
			overlay:       '<div />',

			// Menu
			menuObject:    '<div />',
			menuWrapper:   '<div />',
			menuContainer: '<nav />',
			menuHeader:    '<header />',
			menuTitle:     '<div />',
			menuMask:      '<div />',
			menuList:      '<ul role="menu" />',
			menuItem:      '<li role="presentation" />',
			menuLabel:     '<li role="presentation" />',
			menuDivider:   '<li role="presentation" />',
			menuLink:      '<a href="#" role="menuitem" />',
			menuText:      '<span />',

			// Toggle
			toggleButton:  '<a href="#" />',
			toggleText:    '<span />',
			toggleIcon:    '<span />',

			// Back
			backButton:    '<a href="#" title="Back" />',
			backText:      '<span />',
			backIcon:      '<span />',

			// Close
			closeButton:   '<a href="#" title="Close" />',
			closeText:     '<span />',
			closeIcon:     '<span />',

			// Icons
			iconPrev:      '<span />',
			iconNext:      '<span />'

		},


		/**
		 *
		 *	Classes
		 *
		 *	================================================================ */

		classes: {

			dropdown:       'dropdown',
			overlay:        'dropdown-overlay',

			// Menu
			mainMenu:       'dropdown-main-menu',
			menuObject:     'dropdown-menu',
			menuWrapper:    'dropdown-menu-wrapper',
			menuContainer:  'dropdown-menu-container',
			menuHeader:     'dropdown-header',
			menuTitle:      'dropdown-title',
			menuLabel:      'dropdown-label',
			menuDivider:    'dropdown-divider',
			menuMask:       'dropdown-mask',
			menuParent:     'dropdown-parent',
			menuChild:      'dropdown-child',
			menuList:       'dropdown-list',
			menuItem:       'dropdown-item',

			menuLink:       'dropdown-link',
			menuText:       'dropdown-text',
			menuAbove:      'dropdown-above',

			// Back
			backButton:     'dropdown-back',
			backText:       'dropdown-text',
			backIcon:       'dropdown-icon',

			// Toggle
			toggleButton:   'dropdown-toggle',
			toggleText:     'dropdown-text',
			toggleIcon:     'dropdown-icon',

			// Close
			closeButton:    'dropdown-close',
			closeText:      'dropdown-text',
			closeIcon:      'dropdown-icon',

			// Icons
			iconPrev:       'dropdown-icon',
			iconNext:       'dropdown-icon',

			// States
			above:          'dropdown-above',
			below:          'dropdown-below',

			open:           'dropdown-open',
			menuOpen:       'dropdown-menu-open',
			closed:         'dropdown-closed',
			disabled:       'dropdown-disabled',

			opening:        'dropdown-opening',
			closing:        'dropdown-closing',
			animating:      'dropdown-animating',
			resize:         'dropdown-resizing',
			loading:        'dropdown-loading',
			menuOpening:    'dropdown-menu-opening',
			menuClosing:    'dropdown-menu-closing',

			selected:       'dropdown-selected',
			selectedParent: 'dropdown-parent-selected',

			focused:        'dropdown-focus',

			pageOpen:       'dropdown-is-open',
			pageOpenBefore: 'dropdown-before-open',
			pageOpening:    'dropdown-is-opening',
			pageClosing:    'dropdown-is-closing'

		}


	};


	/**
	 *
	 *	Plugin wrapper
	 *
	 *	================================================================ */

	$.fn.dropdown = function(options) {

		var args = arguments;

		if ( options === undefined || typeof options === 'object' ) {

			return this.each( function() {

				if ( !$.data( this, 'dw.plugin.dropdown' ) ) {
					$.data( this, 'dw.plugin.dropdown', new dropdown(this, options) );
				}

			});

		} else if ( typeof options === 'string' && options[0] !== '_' && options !== 'init' ) {

			var returns;

			this.each( function() {

				var instance = $.data( this, 'dw.plugin.dropdown' );

				// Allow access to public methods
				if ( instance instanceof dropdown && typeof instance[options] === 'function' ) {
					returns = instance[options].apply( instance, Array.prototype.slice.call( args, 1 ) );
				}

				// Allow instances to be destroyed via the 'destroy' method
				if ( options === 'destroy' ) {
					$.data( this, 'dw.plugin.dropdown', null );
				}

			});

			return returns !== undefined ? returns : this;

		}

	};


	if ( !window.dw ) window.dw = {};
	window.dw.dropdown = dropdown;


})( jQuery, window, document );



/*!
* jQuery Transit - CSS3 transitions and transformations
* (c) 2011-2014 Rico Sta. Cruz
* MIT Licensed.
*
* http://ricostacruz.com/jquery.transit
* http://github.com/rstacruz/jquery.transit
*/

(function(t,e){if(typeof define==="function"&&define.amd){define(["jquery"],e)}else if(typeof exports==="object"){module.exports=e(require("jquery"))}else{e(t.jQuery)}})(this,function(t){t.transit={version:"0.9.12",propertyMap:{marginLeft:"margin",marginRight:"margin",marginBottom:"margin",marginTop:"margin",paddingLeft:"padding",paddingRight:"padding",paddingBottom:"padding",paddingTop:"padding"},enabled:true,useTransitionEnd:false};var e=document.createElement("div");var n={};function i(t){if(t in e.style)return t;var n=["Moz","Webkit","O","ms"];var i=t.charAt(0).toUpperCase()+t.substr(1);for(var r=0;r<n.length;++r){var s=n[r]+i;if(s in e.style){return s}}}function r(){e.style[n.transform]="";e.style[n.transform]="rotateY(90deg)";return e.style[n.transform]!==""}var s=navigator.userAgent.toLowerCase().indexOf("chrome")>-1;n.transition=i("transition");n.transitionDelay=i("transitionDelay");n.transform=i("transform");n.transformOrigin=i("transformOrigin");n.filter=i("Filter");n.transform3d=r();var a={transition:"transitionend",MozTransition:"transitionend",OTransition:"oTransitionEnd",WebkitTransition:"webkitTransitionEnd",msTransition:"MSTransitionEnd"};var o=n.transitionEnd=a[n.transition]||null;for(var u in n){if(n.hasOwnProperty(u)&&typeof t.support[u]==="undefined"){t.support[u]=n[u]}}e=null;t.cssEase={_default:"ease","in":"ease-in",out:"ease-out","in-out":"ease-in-out",snap:"cubic-bezier(0,1,.5,1)",easeInCubic:"cubic-bezier(.550,.055,.675,.190)",easeOutCubic:"cubic-bezier(.215,.61,.355,1)",easeInOutCubic:"cubic-bezier(.645,.045,.355,1)",easeInCirc:"cubic-bezier(.6,.04,.98,.335)",easeOutCirc:"cubic-bezier(.075,.82,.165,1)",easeInOutCirc:"cubic-bezier(.785,.135,.15,.86)",easeInExpo:"cubic-bezier(.95,.05,.795,.035)",easeOutExpo:"cubic-bezier(.19,1,.22,1)",easeInOutExpo:"cubic-bezier(1,0,0,1)",easeInQuad:"cubic-bezier(.55,.085,.68,.53)",easeOutQuad:"cubic-bezier(.25,.46,.45,.94)",easeInOutQuad:"cubic-bezier(.455,.03,.515,.955)",easeInQuart:"cubic-bezier(.895,.03,.685,.22)",easeOutQuart:"cubic-bezier(.165,.84,.44,1)",easeInOutQuart:"cubic-bezier(.77,0,.175,1)",easeInQuint:"cubic-bezier(.755,.05,.855,.06)",easeOutQuint:"cubic-bezier(.23,1,.32,1)",easeInOutQuint:"cubic-bezier(.86,0,.07,1)",easeInSine:"cubic-bezier(.47,0,.745,.715)",easeOutSine:"cubic-bezier(.39,.575,.565,1)",easeInOutSine:"cubic-bezier(.445,.05,.55,.95)",easeInBack:"cubic-bezier(.6,-.28,.735,.045)",easeOutBack:"cubic-bezier(.175, .885,.32,1.275)",easeInOutBack:"cubic-bezier(.68,-.55,.265,1.55)"};t.cssHooks["transit:transform"]={get:function(e){return t(e).data("transform")||new f},set:function(e,i){var r=i;if(!(r instanceof f)){r=new f(r)}if(n.transform==="WebkitTransform"&&!s){e.style[n.transform]=r.toString(true)}else{e.style[n.transform]=r.toString()}t(e).data("transform",r)}};t.cssHooks.transform={set:t.cssHooks["transit:transform"].set};t.cssHooks.filter={get:function(t){return t.style[n.filter]},set:function(t,e){t.style[n.filter]=e}};if(t.fn.jquery<"1.8"){t.cssHooks.transformOrigin={get:function(t){return t.style[n.transformOrigin]},set:function(t,e){t.style[n.transformOrigin]=e}};t.cssHooks.transition={get:function(t){return t.style[n.transition]},set:function(t,e){t.style[n.transition]=e}}}p("scale");p("scaleX");p("scaleY");p("translate");p("rotate");p("rotateX");p("rotateY");p("rotate3d");p("perspective");p("skewX");p("skewY");p("x",true);p("y",true);function f(t){if(typeof t==="string"){this.parse(t)}return this}f.prototype={setFromString:function(t,e){var n=typeof e==="string"?e.split(","):e.constructor===Array?e:[e];n.unshift(t);f.prototype.set.apply(this,n)},set:function(t){var e=Array.prototype.slice.apply(arguments,[1]);if(this.setter[t]){this.setter[t].apply(this,e)}else{this[t]=e.join(",")}},get:function(t){if(this.getter[t]){return this.getter[t].apply(this)}else{return this[t]||0}},setter:{rotate:function(t){this.rotate=b(t,"deg")},rotateX:function(t){this.rotateX=b(t,"deg")},rotateY:function(t){this.rotateY=b(t,"deg")},scale:function(t,e){if(e===undefined){e=t}this.scale=t+","+e},skewX:function(t){this.skewX=b(t,"deg")},skewY:function(t){this.skewY=b(t,"deg")},perspective:function(t){this.perspective=b(t,"px")},x:function(t){this.set("translate",t,null)},y:function(t){this.set("translate",null,t)},translate:function(t,e){if(this._translateX===undefined){this._translateX=0}if(this._translateY===undefined){this._translateY=0}if(t!==null&&t!==undefined){this._translateX=b(t,"px")}if(e!==null&&e!==undefined){this._translateY=b(e,"px")}this.translate=this._translateX+","+this._translateY}},getter:{x:function(){return this._translateX||0},y:function(){return this._translateY||0},scale:function(){var t=(this.scale||"1,1").split(",");if(t[0]){t[0]=parseFloat(t[0])}if(t[1]){t[1]=parseFloat(t[1])}return t[0]===t[1]?t[0]:t},rotate3d:function(){var t=(this.rotate3d||"0,0,0,0deg").split(",");for(var e=0;e<=3;++e){if(t[e]){t[e]=parseFloat(t[e])}}if(t[3]){t[3]=b(t[3],"deg")}return t}},parse:function(t){var e=this;t.replace(/([a-zA-Z0-9]+)\((.*?)\)/g,function(t,n,i){e.setFromString(n,i)})},toString:function(t){var e=[];for(var i in this){if(this.hasOwnProperty(i)){if(!n.transform3d&&(i==="rotateX"||i==="rotateY"||i==="perspective"||i==="transformOrigin")){continue}if(i[0]!=="_"){if(t&&i==="scale"){e.push(i+"3d("+this[i]+",1)")}else if(t&&i==="translate"){e.push(i+"3d("+this[i]+",0)")}else{e.push(i+"("+this[i]+")")}}}}return e.join(" ")}};function c(t,e,n){if(e===true){t.queue(n)}else if(e){t.queue(e,n)}else{t.each(function(){n.call(this)})}}function l(e){var i=[];t.each(e,function(e){e=t.camelCase(e);e=t.transit.propertyMap[e]||t.cssProps[e]||e;e=h(e);if(n[e])e=h(n[e]);if(t.inArray(e,i)===-1){i.push(e)}});return i}function d(e,n,i,r){var s=l(e);if(t.cssEase[i]){i=t.cssEase[i]}var a=""+y(n)+" "+i;if(parseInt(r,10)>0){a+=" "+y(r)}var o=[];t.each(s,function(t,e){o.push(e+" "+a)});return o.join(", ")}t.fn.transition=t.fn.transit=function(e,i,r,s){var a=this;var u=0;var f=true;var l=t.extend(true,{},e);if(typeof i==="function"){s=i;i=undefined}if(typeof i==="object"){r=i.easing;u=i.delay||0;f=typeof i.queue==="undefined"?true:i.queue;s=i.complete;i=i.duration}if(typeof r==="function"){s=r;r=undefined}if(typeof l.easing!=="undefined"){r=l.easing;delete l.easing}if(typeof l.duration!=="undefined"){i=l.duration;delete l.duration}if(typeof l.complete!=="undefined"){s=l.complete;delete l.complete}if(typeof l.queue!=="undefined"){f=l.queue;delete l.queue}if(typeof l.delay!=="undefined"){u=l.delay;delete l.delay}if(typeof i==="undefined"){i=t.fx.speeds._default}if(typeof r==="undefined"){r=t.cssEase._default}i=y(i);var p=d(l,i,r,u);var h=t.transit.enabled&&n.transition;var b=h?parseInt(i,10)+parseInt(u,10):0;if(b===0){var g=function(t){a.css(l);if(s){s.apply(a)}if(t){t()}};c(a,f,g);return a}var m={};var v=function(e){var i=false;var r=function(){if(i){a.unbind(o,r)}if(b>0){a.each(function(){this.style[n.transition]=m[this]||null})}if(typeof s==="function"){s.apply(a)}if(typeof e==="function"){e()}};if(b>0&&o&&t.transit.useTransitionEnd){i=true;a.bind(o,r)}else{window.setTimeout(r,b)}a.each(function(){if(b>0){this.style[n.transition]=p}t(this).css(l)})};var z=function(t){this.offsetWidth;v(t)};c(a,f,z);return this};function p(e,i){if(!i){t.cssNumber[e]=true}t.transit.propertyMap[e]=n.transform;t.cssHooks[e]={get:function(n){var i=t(n).css("transit:transform");return i.get(e)},set:function(n,i){var r=t(n).css("transit:transform");r.setFromString(e,i);t(n).css({"transit:transform":r})}}}function h(t){return t.replace(/([A-Z])/g,function(t){return"-"+t.toLowerCase()})}function b(t,e){if(typeof t==="string"&&!t.match(/^[\-0-9\.]+$/)){return t}else{return""+t+e}}function y(e){var n=e;if(typeof n==="string"&&!n.match(/^[\-0-9\.]+/)){n=t.fx.speeds[n]||t.fx.speeds._default}return b(n,"ms")}t.transit.getTransitionValue=d;return t});
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])

  const totalItems = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  function addItem(menuItem) {
    const existing = items.value.find(i => i.menuItemId === menuItem.id)
    if (existing) {
      existing.quantity++
    } else {
      items.value.push({
        menuItemId: menuItem.id,
        name: menuItem.name,
        price: menuItem.price,
        quantity: 1
      })
    }
  }

  function removeItem(menuItemId) {
    const index = items.value.findIndex(i => i.menuItemId === menuItemId)
    if (index > -1) {
      items.value.splice(index, 1)
    }
  }

  function updateQuantity(menuItemId, quantity) {
    const item = items.value.find(i => i.menuItemId === menuItemId)
    if (item) {
      if (quantity <= 0) {
        removeItem(menuItemId)
      } else {
        item.quantity = quantity
      }
    }
  }

  function clearCart() {
    items.value = []
  }

  function getItemQuantity(menuItemId) {
    const item = items.value.find(i => i.menuItemId === menuItemId)
    return item ? item.quantity : 0
  }

  return {
    items,
    totalItems,
    totalPrice,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    getItemQuantity
  }
})

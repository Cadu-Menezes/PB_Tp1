<template>
  <v-app>
    <v-main>
      <v-app-bar color="teal-darken-4" density="comfortable" flat>
        <v-app-bar-title>Pedidos do FastFood</v-app-bar-title>
        <template #append>
          <v-chip variant="tonal" color="white" class="text-teal-darken-4">
            Vue + Vuetify
          </v-chip>
        </template>
      </v-app-bar>
      <v-container class="pa-4 pa-md-8 app-fundo">
        <v-row justify="center">
          <v-col cols="12" md="10" lg="8">
            <v-card elevation="12" rounded="xl" class="pa-4 pa-md-6 card-principal">
              <v-row align="center" class="mb-4">
                <v-col cols="12" md="8">
                  <v-card-title class="text-h4 font-weight-bold pa-0">
                    Pedidos
                  </v-card-title>
                  <v-card-subtitle class="pa-0 mt-2">
                    lista de pedidos
                  </v-card-subtitle>
                </v-col>

                <v-col cols="12" md="4" class="text-md-right">
                  <v-chip color="teal-darken-2" variant="flat" class="ma-1">
                    {{ pedidos.length }} pedidos
                  </v-chip>
                  <v-chip color="amber-darken-2" variant="flat" class="ma-1">
                    {{ pedidosPendentes.length }} pendentes
                  </v-chip>
                </v-col>
              </v-row>

              <v-alert
                v-if="mensagem"
                :type="tipoMensagem"
                variant="tonal"
                class="mb-4"
                border="start"
              >
                {{ mensagem }}
              </v-alert>

              <v-form @submit.prevent="criarPedido" class="mb-4">
                <v-row dense>
                  <v-col cols="12" md="9">
                    <v-text-field
                      v-model="descricao"
                      label="Descrição do pedido"
                      placeholder="Ex.: 2 hambúrgueres e 1 refrigerante"
                      variant="outlined"
                      density="comfortable"
                      prepend-inner-icon="mdi-receipt-text-plus"
                      :disabled="carregando"
                      hide-details="auto"
                    />
                  </v-col>

                  <v-col cols="12" md="3" class="d-flex align-center">
                    <v-btn
                      type="submit"
                      color="teal-darken-2"
                      size="large"
                      block
                      :loading="carregando"
                      :disabled="carregando"
                    >
                      Adicionar pedido
                    </v-btn>
                  </v-col>
                </v-row>
              </v-form>

              <v-progress-linear
                v-if="carregando"
                indeterminate
                color="teal-darken-2"
                rounded
                class="mb-4"
              />

              <v-divider class="mb-4" />

              <v-list v-if="pedidos.length > 0" lines="two" class="bg-transparent pa-0">
                <v-list-item
                  v-for="pedido in pedidos"
                  :key="pedido.id"
                  rounded="lg"
                  class="mb-3 pedido-item"
                  :class="pedido.finalizado ? 'pedido-finalizado' : 'pedido-pendente'"
                >
                  <template #prepend>
                    <v-avatar :color="pedido.finalizado ? 'green-darken-2' : 'orange-darken-2'" size="40">
                      <v-icon icon="mdi-food" color="white" />
                    </v-avatar>
                  </template>

                  <template #title>
                    <span :class="pedido.finalizado ? 'texto-finalizado' : ''">
                      {{ pedido.descricao }}
                    </span>
                  </template>

                  <template #subtitle>
                    <span v-if="pedido.finalizado" class="text-green-darken-2">Finalizado</span>
                    <span v-else class="text-orange-darken-2">Pendente</span>
                  </template>

                  <template #append>
                    <v-btn
                      v-if="!pedido.finalizado"
                      color="teal-darken-2"
                      variant="flat"
                      :loading="pedidoEmAcao === pedido.id"
                      @click="finalizarPedido(pedido.id)"
                    >
                      Finalizar
                    </v-btn>
                  </template>
                </v-list-item>
              </v-list>

              <v-sheet v-else rounded="lg" class="pa-6 text-center vazio-caixa">
                <v-icon icon="mdi-clipboard-text-outline" size="48" color="teal-darken-2" class="mb-3" />
                <div class="text-h6 mb-1">Nenhum pedido cadastrado</div>
                <div class="text-body-2 text-medium-emphasis">
                  Adicione o primeiro pedido usando o formulário acima.
                </div>
              </v-sheet>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>

</template>

<script setup>

  import { computed, onMounted, ref } from 'vue';

  const pedidos = ref([]);
  const descricao = ref('');
  const mensagem = ref('');
  const tipoMensagem = ref('success');
  const carregando = ref(false);
  const pedidoEmAcao = ref(null);

  const basesApi = [
    '/api/pedidos',
    'http://localhost:8081/api/pedidos'
  ];

  const pedidosPendentes = computed(() => pedidos.value.filter((pedido) => !pedido.finalizado));

  async function chamarApi(caminho, opcoes = {}) {
    let ultimoErro = null;

    for (const base of basesApi) {
      try {
        const resposta = await fetch(`${base}${caminho}`, opcoes);

        if (resposta.ok || resposta.status < 500) {
          return resposta;
        }

        ultimoErro = new Error(`Resposta inválida da API: ${resposta.status}`);
      } catch (erro) {
        ultimoErro = erro;
      }
    }

    throw ultimoErro ?? new Error('Falha ao conectar na API.');
  }

  async function carregarPedidos() {
    const resposta = await chamarApi('');
    if (!resposta.ok) {
      throw new Error('Falha ao carregar os pedidos.');
    }

    pedidos.value = await resposta.json();
  }

  async function criarPedido() {
    if (!descricao.value.trim()) {
      tipoMensagem.value = 'warning';
      mensagem.value = 'Digite a descrição do pedido.';
      return;
    }

    carregando.value = true;
    mensagem.value = '';

    try {
      const resposta = await chamarApi('', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ descricao: descricao.value.trim() })
      });

      if (!resposta.ok) {
        throw new Error('Não foi possível criar o pedido.');
      }

      descricao.value = '';
      tipoMensagem.value = 'success';
      mensagem.value = 'Pedido criado com sucesso.';
      await carregarPedidos();
    } catch (erro) {
      tipoMensagem.value = 'error';
      mensagem.value = erro.message;
    } finally {
      carregando.value = false;
    }
  }

  async function finalizarPedido(id) {
    pedidoEmAcao.value = id;
    mensagem.value = '';

    try {
      const resposta = await chamarApi(`/${id}/finalizar`, {
        method: 'PATCH'
      });

      if (!resposta.ok) {
        throw new Error('Não foi possível finalizar o pedido.');
      }

      tipoMensagem.value = 'success';
      mensagem.value = 'Pedido finalizado.';
      await carregarPedidos();
    } catch (erro) {
      tipoMensagem.value = 'error';
      mensagem.value = erro.message;
    } finally {
      pedidoEmAcao.value = null;
    }
  }

  onMounted(async () => {
    carregando.value = true;

    try {
      await carregarPedidos();
    } catch (erro) {
      tipoMensagem.value = 'error';
      mensagem.value = erro.message;
    } finally {
      carregando.value = false;
    }
  });
</script>

<style scoped>

  .app-fundo {
    min-height: calc(100vh - 64px);
  }

  .card-principal {
    border: 1px solid rgba(15, 118, 110, 0.12);
  }

  .pedido-item {
    border: 1px solid rgba(15, 118, 110, 0.12);
  }

  .pedido-pendente {
    background: linear-gradient(90deg, rgba(255, 247, 237, 1) 0%, rgba(255, 255, 255, 1) 24%);
  }

  .pedido-finalizado {
    background: linear-gradient(90deg, rgba(236, 253, 245, 1) 0%, rgba(255, 255, 255, 1) 24%);
  }

  .vazio-caixa {
    border: 1px dashed rgba(15, 118, 110, 0.2);
    background: rgba(255, 255, 255, 0.7);
  }

  .texto-finalizado {
    text-decoration: line-through;
    color: #64748b;
  }
</style>
